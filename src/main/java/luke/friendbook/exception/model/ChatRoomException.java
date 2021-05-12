package luke.friendbook.exception.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ChatRoomException extends RuntimeException{
    private static final long serialVersionUID = 847852985198892550L;

    public ChatRoomException(String message) {
        super(message);
    }
}
