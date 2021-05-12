package luke.friendbook.exception.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class VerificationException extends RuntimeException{
    private static final long serialVersionUID = 3603451038525902847L;

    public VerificationException(String message) {
        super(message);
    }
}
