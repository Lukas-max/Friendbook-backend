package luke.friendbook.account.services;

import luke.friendbook.model.Repository;
import luke.friendbook.account.model.VerificationToken;

import java.util.Optional;

public interface IVerificationTokenRepository extends Repository<VerificationToken> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByUserId(Long userId);
}
