package luke.friendbook.exception.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ArgumentNotValidException extends RuntimeException{
    private static final long serialVersionUID = -6172050402095055722L;

    public ArgumentNotValidException(String message) {
        super(message);
    }
}
