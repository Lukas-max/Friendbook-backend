package luke.friendbook.account.services;

import luke.friendbook.account.model.UserResponseModel;
import luke.friendbook.account.model.UserRequestModel;


public interface IAccountService {

    UserResponseModel register(UserRequestModel userRequestModel);

    void confirmRegistration(String token);

    boolean doesEmailExist(String email);
}
