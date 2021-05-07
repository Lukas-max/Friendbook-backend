package luke.friendbook.account.services;

import luke.friendbook.account.model.*;
import luke.friendbook.exception.*;
import luke.friendbook.mailClient.IMailSender;
import luke.friendbook.mailClient.MailSetting;
import luke.friendbook.storage.services.IFileStorage;
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
import java.util.Optional;

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
            throw new RegistrationException("Brak dostępu do rejestracji dla tego użytkownika.");

        userRepository.findByEmail(user.getEmail()).ifPresent((fetchedUser -> {
            throw new EmailExistsException("Email " + fetchedUser.getEmail() + " już istnieje w bazie");
        }));
    }

    @Override
    public void confirmRegistration(String token) {
        VerificationToken verificationToken = findToken(token);

        if (verificationToken != null && verificationToken.getConfirmationDateTime() != null)
            throw new RegistrationException("Konto już zostało aktywowane!");

        if (verificationToken != null && verificationToken.getExpirationDateTime().isAfter(LocalDateTime.now())){
            User user = userRepository.findByEmail(verificationToken.getUser().getEmail())
                    .orElseThrow(() ->
                            new NotFoundException("Nie znaleziono użytkownika pasującego do wysłanego tokena. " +
                                    "Błąd weryfikacji."));

            user.setActive(true);
            user.setLocked(false);
            user.setAccountCreatedTime(LocalDateTime.now());
            userRepository.save(user);
            verificationToken.setConfirmationDateTime(LocalDateTime.now());
            registrationTokenRepository.save(verificationToken);
            fileStorage.createRegisteredUserStorageDirectory(user);
        }else {
            throw new RegistrationTokenExpirationException("Upłynął czas ważności tokena aktywującego.");
        }
    }


    private VerificationToken findToken(String token) {
        return registrationTokenRepository.findByToken(token).orElseThrow(() -> {
            throw new NotFoundException("Nie znaleziono w bazie tokena.");
        });
    }

    @Override
    public boolean doesEmailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public void sendResetPasswordEmail(String email) {
        Optional<User> optional = userRepository.findByEmail(email);

        if(optional.isPresent()){
            User user = optional.get();
            VerificationToken verificationToken = createVerificationToken(user);
            String template = createMailTemplate(user, resetPassUrl, verificationToken, "resetPassword");

            mailSender.sendMail(user, template, "Prośba o reset hasła");
        }
    }

    @Override
    public void resetPasswordAndNotify(String token) {

    }

    private VerificationToken createVerificationToken(User user) {
        VerificationToken verificationToken = new VerificationToken(user);
        verificationToken.setUser(user);
        registrationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    private String createMailTemplate(User user, String url, VerificationToken verificationToken, String templateForm) {
        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("link", url + "?token=" + verificationToken.getToken());
        return templateEngine.process(templateForm, context);
    }

    private Role getUserRole(RoleType roleType){
        return roleRepository.findByRoleType(roleType)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono roli użytkownika w trakcie tworzenia " +
                        "nowego użytkownika. Błąd serwera."));
    }
}





