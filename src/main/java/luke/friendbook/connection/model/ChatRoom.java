package luke.friendbook.connection.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "sender_uuid")
    private String senderUUID;

    @Column(name = "receiver_uuid")
    private String receiverUUID;
}
