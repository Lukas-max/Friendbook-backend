package luke.friendbook.user.services;

import luke.friendbook.user.model.Role;
import luke.friendbook.user.model.RoleType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
public class RoleRepository implements IRoleRepository{

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Optional<Role> findById(Long id) {
        Role role = entityManager.find(Role.class, id);
        if (role == null)
            return Optional.empty();

        return Optional.of(role);
    }

    @Override
    public Optional<Role> findByRoleType(RoleType roleType) {
        final String query = "SELECT r FROM Role r WHERE r.roleType = ?1";
        TypedQuery<Role> userTypedQuery = entityManager.createQuery(query, Role.class);
        userTypedQuery.setParameter(1, roleType);
        Role role;

        try {
            role = userTypedQuery.getSingleResult();
        } catch (NoResultException e) {
            return Optional.empty();
        }
        return Optional.of(role);
    }

    @Override
    @Transactional
    public Role save(Role role) {
        entityManager.persist(role);
        return role;
    }

    @Override
    public Iterable<Role> saveAll(Iterable<Role> roles) {
        roles.forEach(this::save);
        return roles;
    }

    @Override
    @Transactional
    public Role update(Role role) {
        return null;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        Role role = entityManager.find(Role.class, id);
        if (role != null){
            entityManager.remove(role);
            return true;
        }
        return false;
    }
}
