package luke.friendbook.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Credentials {

    @NotBlank(message = "Pole hasło nie może być puste")
    @Size(min = 5, max = 60, message = "Niepoprawna długość pola hasło")
    private String password;

    @NotBlank(message = "Pole potwierdź hasło nie może być puste")
    @Size(min = 5, max = 60, message = "Niepoprawna długość pola potwierdź hasło")
    private String confirmPassword;
}
