package luke.friendbook.exception.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserUnauthorizedException extends RuntimeException{
    private static final long serialVersionUID = 8413545744102794699L;

    public UserUnauthorizedException(String message) {
        super(message);
    }
}
