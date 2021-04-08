package luke.friendbook.user.services;

import luke.friendbook.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserRepository implements IUserRepository, UserDetailsService {

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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika z emailem " + email));

        return new org.springframework.security.core.userdetails
                .User(
                user.getEmail(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                !user.isLocked(),
                this.getAuthorities(user));
    }

    private Collection<GrantedAuthority> getAuthorities(User user) {
        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleType().toString()))
                .collect(Collectors.toSet());
    }
}







