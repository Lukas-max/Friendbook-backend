package luke.friendbook.exception.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class FileUnreadableException extends RuntimeException{
    private static final long serialVersionUID = -2373941966979404747L;

    public FileUnreadableException(String message) {
        super(message);
    }
}
