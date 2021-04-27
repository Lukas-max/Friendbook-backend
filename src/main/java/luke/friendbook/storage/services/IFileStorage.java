package luke.friendbook.storage.services;

import luke.friendbook.model.Chunk;
import luke.friendbook.account.model.User;
import luke.friendbook.storage.model.DirectoryType;
import luke.friendbook.storage.model.FileData;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface IFileStorage {

    void init() throws IOException;

    byte[] download(String userUUID, String directory, String fileName, DirectoryType dirType);

    File[] findDirectories(String userUUID);

    FileData[] findFiles(String userUUID, String directory) throws IOException;

    Chunk<FileData> findFilesChunk(String userUUID, String directory, int limit, int offset) throws IOException;

    int save(MultipartFile[] files, String directory, DirectoryType dirType);

    void createFolder(String directory);

    boolean deleteFolder(String directory);

    void deleteFile(String directory, String fileName);

    void createRegisteredUserStorageDirectory(User user);

    void cleanAll() throws IOException;
}
