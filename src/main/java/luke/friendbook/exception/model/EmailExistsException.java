package luke.friendbook.exception.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class EmailExistsException extends RuntimeException{
    private static final long serialVersionUID = -8238766094241799498L;

    public EmailExistsException(String message) {
        super(message);
    }
}
