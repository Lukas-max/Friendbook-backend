package luke.friendbook.account.services;

import luke.friendbook.account.model.Credentials;
import luke.friendbook.account.model.UserResponseModel;
import luke.friendbook.account.model.UserRequestModel;


public interface IAccountService {

    UserResponseModel register(UserRequestModel userRequestModel);

    void confirmRegistration(String token);

    boolean doesEmailExist(String email);

    void sendResetPasswordEmail(String email);

    void resetPasswordAndNotify(String token);

    void changePassword(Credentials credentials);

    void changeEmail(String email);

    void deleteAccount();
}
