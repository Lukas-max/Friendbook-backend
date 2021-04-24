package luke.friendbook.utilities;

import luke.friendbook.account.model.Role;
import luke.friendbook.account.model.RoleType;
import luke.friendbook.account.model.User;
import luke.friendbook.account.services.IRoleRepository;
import luke.friendbook.account.services.IUserRepository;
import luke.friendbook.account.services.RoleRepository;
import luke.friendbook.account.services.UserRepository;
import luke.friendbook.mainFeed.services.IFeedStorage;
import luke.friendbook.storage.services.IFileStorage;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

@Component
public class AppBoot {

    private final IUserRepository userDao;
    private final IRoleRepository roleRepository;
    private final IFileStorage fileStorage;
    private final PasswordEncoder passwordEncoder;
    private final IFeedStorage feedStorage;

    public AppBoot(UserRepository userDao,
                   RoleRepository roleRepository,
                   PasswordEncoder passwordEncoder,
                   IFileStorage fileStorage,
                   IFeedStorage feedStorage) {
        this.userDao = userDao;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorage = fileStorage;
        this.feedStorage = feedStorage;
    }

    @EventListener
    public void onApplicationContextInitialization(ContextRefreshedEvent event) throws IOException {
        bootstrapDatabase();
//        fileStorage.init();
        feedStorage.init();
    }

    private void bootstrapDatabase() {
        Role role = new Role();
        role.setRoleType(RoleType.USER);
        roleRepository.save(role);

        Role adminRole = new Role();
        adminRole.setRoleType(RoleType.ADMIN);
        roleRepository.save(adminRole);


        User user1 = User.builder()
                .username("marian")
                .password(passwordEncoder.encode("user"))
                .email("m")
                .isActive(true)
                .isLocked(false)
                .accountCreatedTime(LocalDateTime.now())
                .roles(Set.of(role))
                .build();
        user1.generateUUID();
        userDao.save(user1);

        User user2 = User.builder()
                .username("jarogniew")
                .password(passwordEncoder.encode("user"))
                .email("jarogniew@o2.pl")
                .isActive(true)
                .isLocked(false)
                .accountCreatedTime(LocalDateTime.now())
                .roles(Set.of(role))
                .build();
        user2.generateUUID();
        userDao.save(user2);

        User user3 = User.builder()
                .username("matylda")
                .password(passwordEncoder.encode("user"))
                .email("matylda@o2.pl")
                .isActive(true)
                .isLocked(false)
                .accountCreatedTime(LocalDateTime.now())
                .roles(Set.of(role))
                .build();
        user3.generateUUID();
        userDao.save(user3);
    }
}
