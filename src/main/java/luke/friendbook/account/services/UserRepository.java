package luke.friendbook.account.services;

import luke.friendbook.account.model.User;
import luke.friendbook.exception.model.NotFoundException;
import luke.friendbook.security.model.SecurityContextUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserRepository implements IUserRepository, UserDetailsService {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<User> findAll() {
        final String query = "SELECT u FROM User u ORDER BY u.username";
        TypedQuery<User> userTypedQuery = entityManager.createQuery(query, User.class);
        return userTypedQuery.getResultList();
    }

    @Override
    public List<User> findActiveUsers() {
        final String query = "SELECT u FROM User u WHERE u.isActive = ?1 AND u.isLocked = ?2 ORDER BY u.username";
        TypedQuery<User> userTypedQuery = entityManager.createQuery(query, User.class)
                .setParameter(1, true)
                .setParameter(2, false);
        return userTypedQuery.getResultList();
    }

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
        EntityGraph<User> entityGraph = entityManager.createEntityGraph(User.class);
        entityGraph.addAttributeNodes("roles");

        TypedQuery<User> userTypedQuery = entityManager.createQuery(query, User.class)
                .setParameter(1, username)
                .setHint("javax.persistence.loadgraph", entityGraph);
        return userTypedQuery.getResultList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        final String query = "SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = ?1";
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
    public Optional<User> findByUuid(String userUUID) {
        final String query = "SELECT u FROM User u WHERE u.userUUID = ?1";
        TypedQuery<User> userTypedQuery = entityManager.createQuery(query, User.class);
        userTypedQuery.setParameter(1, userUUID);
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
            fetchedUser.setPassword(user.getPassword());
            fetchedUser.setEmail(user.getEmail());
        } else {
            throw new NotFoundException("Nie znaleziono użytkownika po UUID. (UserRepository.update())");
        }
        return fetchedUser;
    }

    @Override
    @Transactional
    public void patchEmailOrPassword(User user) {
        final String sql = "UPDATE User u SET u.email =?1, u.password =?2 WHERE u.userUUID = ?3";
        Query query = entityManager.createQuery(sql)
                .setParameter(1, user.getEmail())
                .setParameter(2, user.getPassword())
                .setParameter(3, user.getUserUUID());
        query.executeUpdate();
    }

    @Override
    @Transactional
    public User patchStorageSize(String userUUID, float size) {
        User user = findByUuid(userUUID).orElseThrow(() ->
                new NotFoundException("Nie znaleziono zalogowanego użytkownika. (UserRepository.patchStorageSize())"));

        user.setStorageSize(size);
        return user;
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

    @Override
    @Transactional
    public void setUserStatusToLoggedIn(String userUUID) {
        User user = this.findByUuid(userUUID).orElseThrow(() ->
                new NotFoundException("Nie znaleziono użytkownika po uuid, do którego została wysłana wiadomość."));

        user.setLogged(true);
    }

    @Override
    @Transactional
    public void setUserStatusToLoggedOut(String userUUID) {
        this.findByUuid(userUUID)
                .ifPresent((user) -> user.setLogged(false));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika z emailem " + email));

        return new SecurityContextUser(
                user.getEmail(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                !user.isLocked(),
                this.getAuthorities(user),
                user
        );
    }

    private Collection<GrantedAuthority> getAuthorities(User user) {
        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleType().toString()))
                .collect(Collectors.toSet());
    }
}







