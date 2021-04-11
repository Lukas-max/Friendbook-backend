package luke.friendbook.storage;

import luke.friendbook.storage.services.IFileStorage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/api/storage")
public class FileController {

    private final IFileStorage fileStorage;

    public FileController(IFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @GetMapping()
    public ResponseEntity<File[]> getUserDirectories(@RequestParam(required = false) String userUUID) {
        return ResponseEntity.ok().body(fileStorage.findUserDirectories(userUUID));
    }
}
