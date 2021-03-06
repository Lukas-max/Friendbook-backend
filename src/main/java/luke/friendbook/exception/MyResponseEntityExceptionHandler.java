package luke.friendbook.exception;

import luke.friendbook.exception.model.LimitedUploadException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class MyResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        List<String> errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", new Timestamp(System.currentTimeMillis()));
        map.put("status", status.value());
        map.put("message", errorMessages);
        map.put("statusName", status.toString());
        return new ResponseEntity<>(map, headers, status);
    }

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleSizeLimitExceededException(MaxUploadSizeExceededException ex) {

        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", new Timestamp(System.currentTimeMillis()));
        map.put("status", HttpStatus.BAD_REQUEST);
        map.put("statusName", "BAD REQUEST");
        map.put("message", "Mo??esz wys??a?? tylko 64 MB za jednym razem.");
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}
