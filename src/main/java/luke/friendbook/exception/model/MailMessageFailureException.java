package luke.friendbook.exception.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MailMessageFailureException extends RuntimeException{
    private static final long serialVersionUID = 5739161844461622084L;

    public MailMessageFailureException(String message) {
        super(message);
    }
}
