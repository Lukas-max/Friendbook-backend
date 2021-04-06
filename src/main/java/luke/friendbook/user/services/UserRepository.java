package luke.friendbook.user.services;

import luke.friendbook.user.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository implements IUserRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Optional<User> findById(Long userId) {
        User user = entityManager.find(User.class, userId);
        if (user == null)
            return Optional.empty();

        return Optional.of(user);
    }

    @Override
    public List<User> findByUsername(String username) {
        final String query = "SELECT u FROM User u WHERE u.username = ?1";
        TypedQuery<User> userTypedQuery = entityManager.createQuery(query, User.class);
        userTypedQuery.setParameter(1, username);
        return userTypedQuery.getResultList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        final String query = "SELECT u FROM User u WHERE u.email = ?1";
        TypedQuery<User> userTypedQuery = entityManager.createQuery(query, User.class);
        userTypedQuery.setParameter(1, email);
        User user;

        try {
            user = userTypedQuery.getSingleResult();
        } catch (NoResultException e) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    @Transactional
    public User save(User user) {
        entityManager.persist(user);
        return user;
    }

    @Override
    @Transactional
    public Iterable<User> saveAll(Iterable<User> users) {
        users.forEach(this::save);
        return users;
    }

    @Override
    @Transactional
    public User update(User user) {
        User fetchedUser = entityManager.find(User.class, user.getUserId());
        if (fetchedUser != null) {
            fetchedUser.setUsername(user.getUsername());
            fetchedUser.setEmail(user.getEmail());
            fetchedUser.setPassword(user.getPassword());
            fetchedUser.setRole(user.getRole());
        }
        return fetchedUser;
    }

    @Override
    @Transactional
    public boolean deleteById(Long userId) {
        User user = entityManager.find(User.class, userId);
        if (user != null) {
            entityManager.remove(user);
            return true;
        }

        return false;
    }
}







