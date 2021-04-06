package luke.friendbook;

import luke.friendbook.user.model.Role;
import luke.friendbook.user.model.User;
import luke.friendbook.user.services.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class FriendbookApplication {

    private final UserRepository userDao;

    public FriendbookApplication(UserRepository userDao) {
        this.userDao = userDao;
    }

    public static void main(String[] args) {
        SpringApplication.run(FriendbookApplication.class, args);
    }

    @EventListener
    public void onApplicationContextInitialization(ContextRefreshedEvent event) {
        Set<User> users = new HashSet<>();
        User user1 = User.builder()
                .username("Marian")
                .password("user")
                .email("marian@o2.pl")
                .role(Role.USER)
                .isActive(true)
                .build();
        users.add(user1);

        User user2 = User.builder()
                .username("Jarogniew")
                .password("user")
                .email("jarogniew@o2.pl")
                .role(Role.USER)
                .isActive(true)
                .build();
        users.add(user2);

        User user3 = User.builder()
                .username("Matylda")
                .password("user")
                .email("matylda@o2.pl")
                .role(Role.USER)
                .isActive(true)
                .build();
        users.add(user3);

        userDao.saveAll(users);
    }
}
