package luke.friendbook.account.services;

import luke.friendbook.utilities.Repository;
import luke.friendbook.account.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository extends Repository<User> {

    List<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUuid(String userUUID);
}
