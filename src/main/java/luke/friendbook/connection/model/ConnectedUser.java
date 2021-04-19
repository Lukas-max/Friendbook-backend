package luke.friendbook.connection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConnectedUser {
    private String username;
    private String userUUID;
}
