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

@Service
public class AccountService implements IAccountService {

    private final IUserRepository userDao;
    private final IRegistrationTokenRepository registrationTokenRepository;
    private final IRoleRepository roleRepository;
    private final TemplateEngine templateEngine;
    private final IMailSender mailSender;
    private final IFileStorage fileStorage;
    private final PasswordEncoder passwordEncoder;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${app.mail.verification.url}")
    private String url;
    @Value("${app.mail.verification.mode}")
    private String mailMode;

    public AccountService(
            IUserRepository userDao,
            IRegistrationTokenRepository registrationTokenRepository,
            IRoleRepository roleRepository,
            TemplateEngine templateEngine,
            IMailSender mailSender,
            IFileStorage fileStorage,
            PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
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

        if (mailSetting == MailSetting.OFF) userDao.save(user);
        else {
            userDao.save(user);
            RegistrationToken registrationToken = createRegistrationToken(user);
            sendRegistrationToken(user, registrationToken);
        }
        return new ModelMapper().map(user, UserResponseModel.class);
    }

    private void validateRegistration(User user) {
        if (user.getUserId() != null)
            throw new RegistrationException("Brak dostępu do rejestracji dla tego użytkownika.");

        userDao.findByEmail(user.getEmail()).ifPresent((fetchedUser -> {
            throw new EmailExistsException("Email " + fetchedUser.getEmail() + " już istnieje w bazie");
        }));
    }

    private RegistrationToken createRegistrationToken(User user) {
        RegistrationToken registrationToken = new RegistrationToken(user);
        registrationToken.setUser(user);
        registrationTokenRepository.save(registrationToken);
        return registrationToken;
    }

    private void sendRegistrationToken(User user, RegistrationToken registrationToken) {
        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("link", url + "?token=" + registrationToken.getToken());
        String mailContent = templateEngine.process("registerMail", context);

        mailSender.sendRegisterTokenTemplate(user, mailContent);
    }

    @Override
    public void confirmRegistration(String token) {
        RegistrationToken registrationToken = findToken(token);

        if (registrationToken != null && registrationToken.getConfirmationDateTime() != null)
            throw new RegistrationException("Konto już zostało aktywowane!");

        if (registrationToken != null && registrationToken.getExpirationDateTime().isAfter(LocalDateTime.now())){
            User user = userDao.findByEmail(registrationToken.getUser().getEmail())
                    .orElseThrow(() ->
                            new NotFoundException("Nie znaleziono użytkownika pasującego do wysłanego tokena. " +
                                    "Błąd weryfikacji."));

            user.setActive(true);
            user.setLocked(false);
            user.setAccountCreatedTime(LocalDateTime.now());
            userDao.save(user);
            registrationToken.setConfirmationDateTime(LocalDateTime.now());
            registrationTokenRepository.save(registrationToken);
            fileStorage.createRegisteredUserStorageDirectory(user);
        }else {
            throw new RegistrationTokenExpirationException("Upłynął czas ważności tokena aktywującego.");
        }
    }


    private RegistrationToken findToken(String token) {
        return registrationTokenRepository.findByToken(token).orElseThrow(() -> {
            throw new NotFoundException("Nie znaleziono w bazie tokena.");
        });
    }

    @Override
    public boolean doesEmailExist(String email) {
        return userDao.findByEmail(email).isPresent();
    }

    private Role getUserRole(RoleType roleType){
        return roleRepository.findByRoleType(roleType)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono roli użytkownika w trakcie tworzenia " +
                        "nowego użytkownika. Błąd serwera."));
    }
}





