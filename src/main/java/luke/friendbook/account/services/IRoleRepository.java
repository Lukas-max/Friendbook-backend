package luke.friendbook.account.services;

import luke.friendbook.Repository;
import luke.friendbook.account.model.Role;
import luke.friendbook.account.model.RoleType;

import java.util.Optional;

public interface IRoleRepository extends Repository<Role> {

    Optional<Role> findByRoleType(RoleType roleType);
}
