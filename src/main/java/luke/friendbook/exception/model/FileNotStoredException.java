package luke.friendbook.exception.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileNotStoredException extends RuntimeException{
    private static final long serialVersionUID = 8595999440115528359L;

    public FileNotStoredException(String message) {
        super(message);
    }
}
