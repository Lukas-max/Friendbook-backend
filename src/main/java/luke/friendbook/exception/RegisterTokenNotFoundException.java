package luke.friendbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RegisterTokenNotFoundException extends RuntimeException{

    public RegisterTokenNotFoundException(String message) {
        super(message);
    }
}
