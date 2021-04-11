package luke.friendbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DirectoryCreationFailException extends RuntimeException {

    public DirectoryCreationFailException(String message) {
        super(message);
    }
}
