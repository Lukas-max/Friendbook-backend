package luke.friendbook.connection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PrivateChatMessage {

    private String senderUUID;
    private String senderName;
    private String receiverUUID;
    private String receiverName;
    private String content;
    private Timestamp timestamp;
}
