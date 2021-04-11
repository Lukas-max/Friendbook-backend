package luke.friendbook.account.services;

import luke.friendbook.account.model.RegistrationToken;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class RegistrationTokenRepository implements IRegistrationTokenRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RegistrationToken> findAll() {
        final String query = "SELECT r FROM RegistrationToken r";
        TypedQuery<RegistrationToken> userTypedQuery = entityManager.createQuery(query, RegistrationToken.class);
        return userTypedQuery.getResultList();
    }

    @Override
    public Optional<RegistrationToken> findById(Long registrationTokenId) {
        RegistrationToken registrationToken = entityManager.find(RegistrationToken.class, registrationTokenId);
        if (registrationToken == null)
            return Optional.empty();

        return Optional.of(registrationToken);
    }

    @Override
    public Optional<RegistrationToken> findByToken(String token) {
        final String query = "SELECT rt FROM RegistrationToken rt WHERE rt.token = ?1";
        TypedQuery<RegistrationToken> tokenTypedQuery = entityManager.createQuery(query, RegistrationToken.class);
        tokenTypedQuery.setParameter(1, token);
        RegistrationToken registrationToken;

        try {
            registrationToken = tokenTypedQuery.getSingleResult();
        } catch (NoResultException e) {
            return Optional.empty();
        }
        return Optional.of(registrationToken);
    }

    @Override
    @Transactional
    public RegistrationToken save(RegistrationToken registrationToken) {
        entityManager.persist(registrationToken);
        return registrationToken;
    }

    @Override
    @Transactional
    public Iterable<RegistrationToken> saveAll(Iterable<RegistrationToken> tokens) {
        tokens.forEach(this::save);
        return tokens;
    }

    @Override
    @Transactional
    public RegistrationToken update(RegistrationToken registrationToken) {
        return null;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        RegistrationToken registrationToken = entityManager.find(RegistrationToken.class, id);
        if (registrationToken != null){
            entityManager.remove(registrationToken);
            return true;
        }
        return false;
    }
}
