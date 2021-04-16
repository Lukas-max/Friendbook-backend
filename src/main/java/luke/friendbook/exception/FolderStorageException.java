package luke.friendbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class FolderStorageException extends RuntimeException {

    public FolderStorageException(String message) {
        super(message);
    }
}
