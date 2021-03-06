package luke.friendbook.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRequestModel {

    @NotBlank(message = "Pole nazwa użytkownika nie może być puste")
    @Size(min = 4, max = 60, message = "Niepoprawna długość pola nazwa użytkownika")
    private String username;

    @NotBlank(message = "Pole email nie może być puste")
    @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$", message = "Niepoprawny email")
    private String email;

    @NotBlank(message = "Pole hasło nie może być puste")
    @Size(min = 5, max = 60, message = "Niepoprawna długość pola hasło")
    private String password;
}
