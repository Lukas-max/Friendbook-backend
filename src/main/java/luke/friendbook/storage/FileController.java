package luke.friendbook.storage;

import luke.friendbook.storage.model.FileData;
import luke.friendbook.storage.services.IFileStorage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api/storage")
public class FileController {

    private final IFileStorage fileStorage;

    public FileController(IFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @GetMapping
    public ResponseEntity<File[]> getUserDirectories(@RequestParam(required = false) String userUUID) {
        return ResponseEntity.ok().body(fileStorage.findDirectories(userUUID));
    }

    @GetMapping("/files")
    public ResponseEntity<FileData[]> getFileData(@RequestParam String userUUID, @RequestParam String directory) throws IOException {
        String decodedUUID = new String(Base64.getDecoder().decode(userUUID.getBytes()));
        String decodedDirectory = new String(Base64.getDecoder().decode(directory.getBytes()));
        return ResponseEntity.ok().body(fileStorage.findFiles(decodedUUID, decodedDirectory));
    }

    @GetMapping("/file/{id:.+}/{directory:.+}/{fileName:.+}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id,
                                               @PathVariable String directory,
                                               @PathVariable String fileName) {
        byte[] data = fileStorage.download(id, directory, fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "ddano plik " + fileName)
                .body(data);
    }

    @PostMapping
    public ResponseEntity<Integer> uploadFiles(@RequestParam("files") MultipartFile[] files, @RequestParam String directory) {
        int length = fileStorage.save(files, directory);
        return ResponseEntity.ok().body(length);
    }
}














