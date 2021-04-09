package luke.friendbook.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Role implements Serializable {
    private static final long serialVersionUID = 4359611198728288477L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleType roleType;
}
