package luke.friendbook.user.services;

import luke.friendbook.user.model.UserRequestModel;
import luke.friendbook.user.model.UserResponseModel;


public interface IUserService {

    UserResponseModel register(UserRequestModel userRequestModel);

    void confirmRegistration(String token);

    boolean doesEmailExist(String email);
}
