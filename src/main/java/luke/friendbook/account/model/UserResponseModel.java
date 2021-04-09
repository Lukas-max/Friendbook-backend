package luke.friendbook.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponseModel {
    private String username;
    private String email;
    private String password;
    private boolean isActive;
    private boolean isLocked;
}
