package luke.friendbook.account.services;

import luke.friendbook.account.model.VerificationToken;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Repository
public class VerificationTokenRepository implements IVerificationTokenRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<VerificationToken> findAll() {
        final String query = "SELECT r FROM VerificationToken r";
        TypedQuery<VerificationToken> userTypedQuery = entityManager.createQuery(query, VerificationToken.class);
        return userTypedQuery.getResultList();
    }

    @Override
    public Optional<VerificationToken> findById(Long registrationTokenId) {
        VerificationToken verificationToken = entityManager.find(VerificationToken.class, registrationTokenId);
        if (verificationToken == null)
            return Optional.empty();

        return Optional.of(verificationToken);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        final String query = "SELECT rt FROM VerificationToken rt WHERE rt.token = ?1";
        TypedQuery<VerificationToken> tokenTypedQuery = entityManager.createQuery(query, VerificationToken.class);
        tokenTypedQuery.setParameter(1, token);
        VerificationToken verificationToken;

        try {
            verificationToken = tokenTypedQuery.getSingleResult();
        } catch (NoResultException e) {
            return Optional.empty();
        }
        return Optional.of(verificationToken);
    }

    @Override
    @Transactional
    public VerificationToken save(VerificationToken verificationToken) {
        entityManager.persist(verificationToken);
        return verificationToken;
    }

    @Override
    @Transactional
    public Iterable<VerificationToken> saveAll(Iterable<VerificationToken> tokens) {
        tokens.forEach(this::save);
        return tokens;
    }

    @Override
    @Transactional
    public VerificationToken update(VerificationToken verificationToken) {
        return null;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        VerificationToken verificationToken = entityManager.find(VerificationToken.class, id);
        if (verificationToken != null){
            entityManager.remove(verificationToken);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        final String sql = "DELETE FROM VerificationToken vt WHERE vt.user.userId = ?1";
        Query query = entityManager.createQuery(sql)
                .setParameter(1, userId);
        query.executeUpdate();
    }
}
