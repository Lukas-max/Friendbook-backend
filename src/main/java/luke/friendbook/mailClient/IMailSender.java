package luke.friendbook.mailClient;

import luke.friendbook.user.model.User;

public interface IMailSender {

    void sendRegisterTokenTemplate(User user, String template);
}
