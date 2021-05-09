package luke.friendbook.connection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "public_chat")
public class PublicChatMessage implements Serializable {
    private static final long serialVersionUID = -7432633079027978603L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "username")
    private String username;

    @Column(name = "user_uuid")
    private String userUUID;

    @Column(name = "timestamp")
    private Timestamp timestamp;
}
