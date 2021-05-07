package luke.friendbook.storage.services;

import luke.friendbook.model.Chunk;
import luke.friendbook.account.model.User;
import luke.friendbook.storage.model.DirectoryType;
import luke.friendbook.storage.model.FileData;
import luke.friendbook.storage.model.FileQuality;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface IFileStorage {

    void init() throws IOException;

    byte[] download(String userUUID, String directory, String fileName, DirectoryType dirType);

    byte[] downloadProfilePhoto(String userUUID, FileQuality quality) throws IOException;

    File[] findDirectories(String userUUID);

    FileData[] findFiles(String userUUID, String directory) throws IOException;

    Chunk<FileData> findFilesChunk(String userUUID, String directory, int limit, int offset) throws IOException;

    void changeProfilePhoto(MultipartFile file);

    void deleteProfilePhoto();

    int save(MultipartFile[] files, String directory, DirectoryType dirType);

    void createFolder(String directory);

    boolean deleteFolder(String directory);

    void deleteFile(String directory, String fileName);

    void createRegisteredUserStorageDirectory(User user);

    void cleanAll() throws IOException;

    void checkStorageSpace();
}
