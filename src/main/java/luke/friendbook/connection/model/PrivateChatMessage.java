package luke.friendbook.connection.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import luke.friendbook.account.model.User;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "private_chat")
public class PrivateChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "sender_uuid")
    private String senderUUID;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "receiver_uuid")
    private String receiverUUID;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "content")
    private String content;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
}
