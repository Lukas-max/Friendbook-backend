package luke.friendbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class FileUnreadableException extends RuntimeException{

    public FileUnreadableException(String message) {
        super(message);
    }
}
