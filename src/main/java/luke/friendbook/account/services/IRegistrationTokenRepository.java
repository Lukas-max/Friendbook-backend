package luke.friendbook.account.services;

import luke.friendbook.utilities.Repository;
import luke.friendbook.account.model.RegistrationToken;

import java.util.Optional;

public interface IRegistrationTokenRepository extends Repository<RegistrationToken> {

    Optional<RegistrationToken> findByToken(String token);
}
