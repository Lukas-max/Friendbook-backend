package luke.friendbook.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registration_token")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RegistrationToken implements Serializable {
    private static final long serialVersionUID = -8911203220166321286L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    private String token;

    private LocalDateTime creationDateTime;

    private LocalDateTime expirationDateTime;

    private LocalDateTime confirmationDateTime;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public RegistrationToken(User user) {
        this.user = new User(user);
        token = UUID.randomUUID().toString();
        creationDateTime = LocalDateTime.now();
        expirationDateTime = creationDateTime.plusDays(1L);
    }

    public void setConfirmationDateTime(LocalDateTime confirmationDateTime) {
        this.confirmationDateTime = confirmationDateTime;
    }

    public void setUser(User user) {
        this.user = new User(user);
    }
}
