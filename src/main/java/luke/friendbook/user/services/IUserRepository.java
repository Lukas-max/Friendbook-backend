package luke.friendbook.user.services;

import luke.friendbook.Repository;
import luke.friendbook.user.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository extends Repository<User> {

    List<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
