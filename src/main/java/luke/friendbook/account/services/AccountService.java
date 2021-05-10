package luke.friendbook.account.services;

import luke.friendbook.account.model.*;
import luke.friendbook.exception.*;
import luke.friendbook.mailClient.IMailSender;
import luke.friendbook.mailClient.MailSetting;
import luke.friendbook.storage.services.IFileStorage;
import luke.friendbook.utilities.Utils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AccountService implements IAccountService {

    private final IUserRepository userRepository;
    private final IRegistrationTokenRepository registrationTokenRepository;
    private final IRoleRepository roleRepository;
    private final TemplateEngine templateEngine;
    private final IMailSender mailSender;
    private final IFileStorage fileStorage;
    private final PasswordEncoder passwordEncoder;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${app.mail.verification.url}")
    private String verifyMailUrl;
    @Value("${app.mail.reset-password.url}")
    private String resetPassUrl;
    @Value("${app.mail.verification.mode}")
    private String mailMode;

    public AccountService(
            IUserRepository userRepository,
            IRegistrationTokenRepository registrationTokenRepository,
            IRoleRepository roleRepository,
            TemplateEngine templateEngine,
            IMailSender mailSender,
            IFileStorage fileStorage,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.registrationTokenRepository = registrationTokenRepository;
        this.roleRepository = roleRepository;
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
        this.fileStorage = fileStorage;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserResponseModel register(UserRequestModel userRequestModel) {
        Role role = getUserRole(RoleType.USER);
        User user = new ModelMapper().map(userRequestModel, User.class);
        user.generateUUID();
        user.getRoles().add(role);
        user.setLocked(true);
        String decodedPassword = new String(Base64.getDecoder().decode(user.getPassword().getBytes()));
        user.setPassword(passwordEncoder.encode(decodedPassword));
        validateRegistration(user);
        MailSetting mailSetting = mailMode.equals("ON") ? MailSetting.ON : MailSetting.OFF;

        if (mailSetting == MailSetting.OFF) userRepository.save(user);
        else {
            userRepository.save(user);
            VerificationToken verificationToken = createVerificationToken(user);
            String mailTemplate = createMailTemplate(user, verifyMailUrl, verificationToken, "registerMail");

            mailSender.sendRegisterTokenTemplate(user, mailTemplate);
        }
        return new ModelMapper().map(user, UserResponseModel.class);
    }

    private void validateRegistration(User user) {
        if (user.getUserId() != null)
            throw new VerificationException("Brak dostępu do rejestracji dla tego użytkownika.");

        userRepository.findByEmail(user.getEmail()).ifPresent((fetchedUser -> {
            throw new EmailExistsException("Email " + fetchedUser.getEmail() + " już istnieje w bazie");
        }));
    }

    @Override
    public void confirmRegistration(String token) {
        VerificationToken verificationToken = findToken(token);
        validateToken(verificationToken);
        User user = userRepository.findByEmail(verificationToken.getUser().getEmail())
                .orElseThrow(() ->
                        new NotFoundException("Nie znaleziono użytkownika pasującego do wysłanego tokena. " +
                                "Błąd weryfikacji."));

        user.setActive(true);
        user.setLocked(false);
        user.setAccountCreatedTime(LocalDateTime.now());
        verificationToken.setConfirmationDateTime(LocalDateTime.now());
        userRepository.save(user);
        registrationTokenRepository.save(verificationToken);
        fileStorage.createRegisteredUserStorageDirectory(user);
    }

    @Override
    public boolean doesEmailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public void sendResetPasswordEmail(String email) {
        Optional<User> optional = userRepository.findByEmail(email);

        if (optional.isPresent()) {
            User user = optional.get();
            VerificationToken verificationToken = createVerificationToken(user);
            String template = createMailTemplate(user, resetPassUrl, verificationToken, "resetPassword");

            mailSender.sendMail(user, template, "Prośba o reset hasła");
        }
    }

    @Override
    public void resetPasswordAndNotify(String token) {
        VerificationToken verificationToken = findToken(token);
        validateToken(verificationToken);

        User user = userRepository.findById(verificationToken.getUser().getUserId())
                .orElseThrow(() -> new NotFoundException("Nie znaleziono w bazie użytkownika. " +
                        "(resetPasswordAndNotify())"));

        verificationToken.setConfirmationDateTime(LocalDateTime.now());
        String password = createRandomPassword();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        registrationTokenRepository.save(verificationToken);
        String mail = createMailWithNewPassword(user, password);
        mailSender.sendMail(user, mail, "Reset hasła");
    }

    @Override
    public void changePassword(Credentials credentials) {
        if (!credentials.getPassword().equals(credentials.getConfirmPassword()))
            throw new VerificationException("Hasła nie zgadzają się ze sobą");

        byte[] passBytes = Base64
                .getDecoder()
                .decode(credentials.getPassword());
        String password = new String(passBytes);
        User user = Utils.getAuthenticatedUser().getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.update(user);
    }

    private VerificationToken createVerificationToken(User user) {
        VerificationToken verificationToken = new VerificationToken(user);
        verificationToken.setUser(user);
        registrationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    private VerificationToken findToken(String token) {
        return registrationTokenRepository.findByToken(token).orElseThrow(() -> {
            throw new NotFoundException("Nie znaleziono w bazie tokena.");
        });
    }

    private void validateToken(VerificationToken verificationToken) {
        if (verificationToken != null && verificationToken.getConfirmationDateTime() != null)
            throw new VerificationException("Token już raz został użyty i jest nie ważny!");

        if (verificationToken.getExpirationDateTime().isBefore(LocalDateTime.now()) ||
                verificationToken.getExpirationDateTime().isEqual(LocalDateTime.now()))
            throw new VerificationTokenExpirationException("Upłynął czas ważności tokena.");
    }

    private String createMailTemplate(User user, String url, VerificationToken verificationToken, String templateForm) {
        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("link", url + "?token=" + verificationToken.getToken());
        return templateEngine.process(templateForm, context);
    }

    private String createMailWithNewPassword(User user, String password) {
        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("pass", password);
        return templateEngine.process("sendPassword", context);
    }

    private String createRandomPassword() {
        Stream<Character> chars = Stream.concat(
                Utils.getRandomChars(8, 85, 125),
                Utils.getRandomChars(8, 33, 84)
        );
        List<Character> listChars = chars.collect(Collectors.toList());
        Collections.shuffle(listChars);
        return listChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private Role getUserRole(RoleType roleType) {
        return roleRepository.findByRoleType(roleType)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono roli użytkownika w trakcie tworzenia " +
                        "nowego użytkownika. Błąd serwera."));
    }

}





