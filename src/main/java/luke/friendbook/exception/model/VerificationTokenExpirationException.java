package luke.friendbook.exception.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class VerificationTokenExpirationException extends RuntimeException{
    private static final long serialVersionUID = 7173891645273869007L;

    public VerificationTokenExpirationException(String message) {
        super(message);
    }
}
