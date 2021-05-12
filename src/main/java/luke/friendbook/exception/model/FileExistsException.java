package luke.friendbook.exception.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class FileExistsException extends RuntimeException{
    private static final long serialVersionUID = 8001025830793537255L;

    public FileExistsException(String message) {
        super(message);
    }
}
