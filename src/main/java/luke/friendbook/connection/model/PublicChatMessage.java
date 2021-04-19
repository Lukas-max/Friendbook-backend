package luke.friendbook.connection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "public_chat")
public class PublicChatMessage {

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
