package luke.friendbook.account.services;

import luke.friendbook.account.model.User;
import luke.friendbook.account.model.UserResponseModel;
import luke.friendbook.exception.model.NotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponseModel> getActiveUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> user.setPassword("SECRET"));
        Type type = new TypeToken<List<UserResponseModel>>(){}.getType();
        return new ModelMapper().map(users, type);
    }

    @Override
    public User getUserByUUID(String uuid) {
        return userRepository.findByUuid(uuid).orElseThrow(() ->
                new NotFoundException("Nie znalazłem użytkownika po numerz UUID"));
    }

    @Override
    public void updateUserStatus(String userUUID, boolean logged) {
        if (logged)
            userRepository.setUserStatusToLoggedIn(userUUID);
        else
            userRepository.setUserStatusToLoggedOut(userUUID);
    }
}
















