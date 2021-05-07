package luke.friendbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class ExceededStorageException extends RuntimeException {

    public ExceededStorageException(String message) {
        super(message);
    }
}
