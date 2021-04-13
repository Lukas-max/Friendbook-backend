package luke.friendbook.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class SecurityContextUser extends User {
    private static final long serialVersionUID = -3252843745339709184L;
    private final luke.friendbook.account.model.User user;

    public SecurityContextUser(String username,
                               String password,
                               boolean enabled,
                               boolean accountNonExpired,
                               boolean credentialsNonExpired,
                               boolean accountNonLocked,
                               Collection<? extends GrantedAuthority> authorities,
                               luke.friendbook.account.model.User user) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.user = user;
    }

    public luke.friendbook.account.model.User getUser() {
        return user;
    }
}
