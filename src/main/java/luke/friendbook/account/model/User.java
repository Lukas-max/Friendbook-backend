package luke.friendbook.account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User implements Serializable {
    private static final long serialVersionUID = -3739367727251499217L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_uuid")
    private String userUUID;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "active", nullable = false)
    private boolean isActive;

    @Column(name = "locked", nullable = false)
    private boolean isLocked;

    @Column(name = "logged", nullable = false)
    private boolean isLogged;

    @Column(name = "account_created_time")
    private LocalDateTime accountCreatedTime;

    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();


    public User(User user) {
        this.userId = user.userId;
        this.userUUID = UUID.randomUUID().toString();
        this.username = user.username;
        this.email = user.email;
        this.password = user.password;
        this.isActive = user.isActive;
        this.isLocked = user.isLocked;
        this.isLogged = user.isLogged;
        this.accountCreatedTime = user.accountCreatedTime;
        this.roles = new HashSet<>(user.getRoles());
    }

    public void generateUUID() {
        this.userUUID = UUID.randomUUID().toString();
    }
}













