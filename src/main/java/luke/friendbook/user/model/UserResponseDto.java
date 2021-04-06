package luke.friendbook.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponseDto {
    private String username;
    private String email;
    private String password;
    private boolean isActive;
}
