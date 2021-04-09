package luke.friendbook;

import luke.friendbook.account.model.Role;
import luke.friendbook.account.model.RoleType;
import luke.friendbook.account.model.User;
import luke.friendbook.account.services.RoleRepository;
import luke.friendbook.account.services.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class FriendbookApplication {

    private final UserRepository userDao;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public FriendbookApplication(UserRepository userDao, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(FriendbookApplication.class, args);
    }

    @EventListener
    public void onApplicationContextInitialization(ContextRefreshedEvent event) {
        Role role = new Role();
        role.setRoleType(RoleType.USER);
        roleRepository.save(role);

        Role adminRole = new Role();
        adminRole.setRoleType(RoleType.ADMIN);
        roleRepository.save(adminRole);


        Set<User> users = new HashSet<>();
        User user1 = User.builder()
                .username("marian")
                .password(passwordEncoder.encode("user"))
                .email("m")
                .isActive(true)
                .isLocked(false)
                .roles(Set.of(role))
                .build();
        users.add(user1);

        User user2 = User.builder()
                .username("jarogniew")
                .password(passwordEncoder.encode("user"))
                .email("jarogniew@o2.pl")
                .isActive(true)
                .isLocked(false)
                .roles(Set.of(role))
                .build();
        users.add(user2);

        User user3 = User.builder()
                .username("matylda")
                .password(passwordEncoder.encode("user"))
                .email("matylda@o2.pl")
                .isActive(true)
                .isLocked(true)
                .roles(Set.of(role))
                .build();
        users.add(user3);

        userDao.saveAll(users);
    }
}
