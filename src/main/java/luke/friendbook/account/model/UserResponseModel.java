package luke.friendbook.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponseModel {
    private String username;
    private String userUUID;
    private String email;
    private String password;
}
