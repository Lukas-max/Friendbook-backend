package luke.friendbook.mailClient;

import luke.friendbook.account.model.User;

public interface IMailSender {

    void sendRegisterTokenTemplate(User user, String template);
}
