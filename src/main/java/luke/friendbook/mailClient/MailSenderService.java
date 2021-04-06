package luke.friendbook.mailClient;

import luke.friendbook.exception.MailMessageFailureException;
import luke.friendbook.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class MailSenderService implements IMailSender {

    private final JavaMailSender javaMailSender;
    private final Logger log = LoggerFactory.getLogger(MailSenderService.class);

    public MailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendRegisterTokenTemplate(User user, String template) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

        try {
            messageHelper.setFrom("appapplication88@gmail.com", "FriendBook");
            messageHelper.setTo(user.getEmail());
            messageHelper.setText(template, true);
            messageHelper.setSubject("Rejestracja w FriendBook - weryfikacja");
            javaMailSender.send(mimeMessage);
            log.info("Sent verification token. Mail: " + user.getEmail() + " User: " + user.getUsername());
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error(e.getLocalizedMessage(), e.getCause());
            throw new MailMessageFailureException("Nie udało się wysłać maila z tokenem weryfikującym." +
                    "Spróbuj jeszcze raz, albo skontaktuj się z administratorem.");
        }
    }
}







