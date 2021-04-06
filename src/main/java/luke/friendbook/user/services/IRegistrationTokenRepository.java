package luke.friendbook.user.services;

import luke.friendbook.Repository;
import luke.friendbook.user.model.RegistrationToken;

import java.util.Optional;

public interface IRegistrationTokenRepository extends Repository<RegistrationToken> {

    Optional<RegistrationToken> findByToken(String token);
}
