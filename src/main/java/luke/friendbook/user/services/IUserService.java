package luke.friendbook.user.services;

import luke.friendbook.user.model.UserRequestDto;
import luke.friendbook.user.model.UserResponseDto;


public interface IUserService {

    UserResponseDto register(UserRequestDto userRequestDto);

    void confirmRegistration(String token);
}
