package luke.friendbook.exception.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class FolderStorageException extends RuntimeException {
    private static final long serialVersionUID = -8606054344100193042L;

    public FolderStorageException(String message) {
        super(message);
    }
}
