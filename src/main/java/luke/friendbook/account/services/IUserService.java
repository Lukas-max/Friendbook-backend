package luke.friendbook.account.services;

import luke.friendbook.account.model.User;
import luke.friendbook.account.model.UserResponseModel;

import java.util.List;

public interface IUserService {

    List<UserResponseModel> getAllUsers();

    UserResponseModel getUserByUUID(String uuid);
}