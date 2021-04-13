package luke.friendbook.account.services;

import luke.friendbook.account.model.User;
import luke.friendbook.account.model.UserResponseModel;
import luke.friendbook.exception.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public UserService(UserRepository userRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public List<UserResponseModel> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> user.setPassword("SECRET"));
        Type type = new TypeToken<List<UserResponseModel>>(){}.getType();
        return new ModelMapper().map(users, type);
    }

    @Override
    public UserResponseModel getUserByUUID(String uuid) {
        User user = userRepository.findByUuid(uuid).orElseThrow(() ->
                new UserNotFoundException("Nie znalazłem użytkownika po numerz UUID"));

        return mapper.map(user, UserResponseModel.class);
    }
}
















