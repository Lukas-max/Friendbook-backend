package luke.friendbook.storage;

import luke.friendbook.model.Chunk;
import luke.friendbook.storage.model.BytePackage;
import luke.friendbook.storage.model.DirectoryType;
import luke.friendbook.storage.model.FileData;
import luke.friendbook.storage.model.FileQuality;
import luke.friendbook.storage.services.IFileStorage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

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
    public ResponseEntity<Chunk<FileData>> getFileData(@RequestParam String userUUID,
                                                       @RequestParam String directory,
                                                       @RequestParam String limit,
                                                       @RequestParam String offset) throws IOException {
        Chunk<FileData> fileChunk = fileStorage.findFilesChunk(
                userUUID,
                directory,
                Integer.parseInt(limit),
                Integer.parseInt(offset));

        return ResponseEntity.ok().body(fileChunk);
    }

    @GetMapping("/file/{id:.+}/{directory:.+}/{fileName:.+}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id,
                                               @PathVariable String directory,
                                               @PathVariable String fileName) {
        byte[] data = fileStorage.download(id, directory, fileName, DirectoryType.STANDARD_DIRECTORY);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "dodano plik " + fileName)
                .body(data);
    }

    @GetMapping("/image/{id:.+}/{directory:.+}/{fileName:.+}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable String id,
                                                @PathVariable String directory,
                                                @PathVariable String fileName) {
        byte[] data = fileStorage.download(id, directory, fileName, DirectoryType.IMAGE_DIRECTORY);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "dodano plik " + fileName)
                .body(data);
    }

    @GetMapping("/profile/high-quality/{userUUID}")
    public ResponseEntity<BytePackage> downloadProfileHighQuality(@PathVariable String userUUID) throws IOException {
        byte[] profilePhoto = fileStorage.downloadProfilePhoto(userUUID, FileQuality.HIGH);

        return ResponseEntity.ok().body(new BytePackage(profilePhoto));
    }

    @GetMapping("/profile/low-quality/{userUUID}")
    public ResponseEntity<BytePackage> downloadProfileLowQuality(@PathVariable String userUUID) throws IOException {
        byte[] profilePhoto = fileStorage.downloadProfilePhoto(userUUID, FileQuality.LOW);

        return ResponseEntity.ok().body(new BytePackage(profilePhoto));
    }

    @PostMapping
    public ResponseEntity<Integer> uploadFiles(@RequestBody MultipartFile[] files, @RequestParam String directory) {
        fileStorage.checkStorageSpace();
        int length = fileStorage.save(files, directory, DirectoryType.STANDARD_DIRECTORY);
        return ResponseEntity.ok().body(length);
    }

    @PostMapping("/images")
    public ResponseEntity<Integer> uploadImages(@RequestBody MultipartFile[] files, @RequestParam String directory) {
        int length = fileStorage.save(files, directory, DirectoryType.IMAGE_DIRECTORY);
        return ResponseEntity.ok().body(length);
    }

    @PostMapping("/profile")
    public ResponseEntity<?> uploadProfilePhoto(@RequestBody MultipartFile photo) {
        fileStorage.changeProfilePhoto(photo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/directory")
    public ResponseEntity<?> createFolder(@RequestBody String directory) {
        fileStorage.createFolder(directory);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{directory}")
    public ResponseEntity<Boolean> deleteFolder(@PathVariable String directory) {
        boolean isDeleted = fileStorage.deleteFolder(directory);
        return ResponseEntity.ok().body(isDeleted);
    }

    @DeleteMapping("/{directory}/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String directory, @PathVariable String fileName) {
        fileStorage.deleteFile(directory, fileName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteProfilePhoto() {
        fileStorage.deleteProfilePhoto();
        return ResponseEntity.ok().build();
    }
}














