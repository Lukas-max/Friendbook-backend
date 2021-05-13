package luke.friendbook.connection.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "chat_room")
public class ChatRoom implements Serializable {
    private static final long serialVersionUID = -2959073730649220030L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(name = "chat_id", nullable = false, unique = true)
    private String chatId;

    @Column(name = "sender_uuid", nullable = false)
    private String senderUUID;

    @Column(name = "receiver_uuid", nullable = false)
    private String receiverUUID;
}
