package luke.friendbook.exception.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DirectoryCreationFailException extends RuntimeException {
    private static final long serialVersionUID = 7248836898293598729L;

    public DirectoryCreationFailException(String message) {
        super(message);
    }
}
