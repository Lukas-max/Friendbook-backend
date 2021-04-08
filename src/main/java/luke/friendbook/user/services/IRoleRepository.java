package luke.friendbook.user.services;

import luke.friendbook.Repository;
import luke.friendbook.user.model.Role;
import luke.friendbook.user.model.RoleType;

import java.util.Optional;

public interface IRoleRepository extends Repository<Role> {

    Optional<Role> findByRoleType(RoleType roleType);
}
