package luke.friendbook.exception.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class ExceededStorageException extends RuntimeException {
    private static final long serialVersionUID = 7146150589144984864L;

    public ExceededStorageException(String message) {
        super(message);
    }
}
