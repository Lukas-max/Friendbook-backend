package luke.friendbook.connection.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import luke.friendbook.account.model.User;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "private_chat")
public class PrivateChatMessage implements Serializable {
    private static final long serialVersionUID = -6798363045676335693L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private String chatId;

    @Column(name = "sender_uuid", nullable = false)
    private String senderUUID;

    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(name = "receiver_uuid", nullable = false)
    private String receiverUUID;

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
}
