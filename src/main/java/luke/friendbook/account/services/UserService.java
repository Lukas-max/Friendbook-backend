package luke.friendbook.account.services;

import luke.friendbook.account.model.User;
import luke.friendbook.account.model.UserResponseModel;
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
    public List<UserResponseModel> getAllUsers() {
        List<User> users = userRepository.findAll();
        Type type = new TypeToken<List<UserResponseModel>>(){}.getType();
        return new ModelMapper().map(users, type);
    }
}
