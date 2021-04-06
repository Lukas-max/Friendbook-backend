package luke.friendbook.user.services;

import luke.friendbook.exception.*;
import luke.friendbook.mailClient.IMailSender;
import luke.friendbook.mailClient.MailSetting;
import luke.friendbook.user.model.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Service
public class UserService implements IUserService {

    private final IUserRepository userDao;
    private final IRegistrationTokenRepository registrationTokenRepository;
    private final TemplateEngine templateEngine;
    private final IMailSender mailSender;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${app.mail.verification.url}")
    private String url;
    @Value("${app.mail.verification.mode}")
    private String mailMode;

    public UserService(
            IUserRepository userDao,
            IRegistrationTokenRepository registrationTokenRepository,
            TemplateEngine templateEngine,
            IMailSender mailSender) {
        this.userDao = userDao;
        this.registrationTokenRepository = registrationTokenRepository;
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }


    @Override
    public UserResponseDto register(UserRequestDto userRequestDto) {
        User user = new ModelMapper().map(userRequestDto, User.class);
        user.setRole(Role.USER);
        validateRegistration(user);
        MailSetting mailSetting = mailMode.equals("ON") ? MailSetting.ON : MailSetting.OFF;

        if (mailSetting == MailSetting.OFF) userDao.save(user);
        else {
            userDao.save(user);
            RegistrationToken registrationToken = createRegistrationToken(user);
            sendRegistrationToken(user, registrationToken);
        }
        return new ModelMapper().map(user, UserResponseDto.class);
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
                            new UserNotFoundException("Nie znaleziono użytkownika pasującego do wysłanego tokena. " +
                                    "Błąd weryfikacji."));

            user.setActive(true);
            registrationToken.setConfirmationDateTime(LocalDateTime.now());
            userDao.save(user);
            registrationTokenRepository.save(registrationToken);
        }else {
            throw new RegistrationTokenExpirationException("Upłynął czas ważności tokena aktywującego.");
        }
    }

    private RegistrationToken findToken(String token) {
        return registrationTokenRepository.findByToken(token).orElseThrow(() -> {
            throw new RegisterTokenNotFoundException("Nie znaleziono w bazie tokena.");
        });
    }

}





