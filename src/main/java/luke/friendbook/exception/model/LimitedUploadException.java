package luke.friendbook.exception.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LimitedUploadException extends RuntimeException{

    public LimitedUploadException(String message) {
        super(message);
    }
}
