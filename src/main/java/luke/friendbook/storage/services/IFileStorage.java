package luke.friendbook.storage.services;

import luke.friendbook.account.model.User;
import luke.friendbook.storage.model.FileData;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface IFileStorage {

    void init() throws IOException;

    byte[] download(String userUUID, String directory, String fileName);

    File[] findDirectories(String userUUID);

    FileData[] findFiles(String userUUID, String directory) throws IOException;

    int save(MultipartFile[] files, String directory);

    void createRegisteredUserStorageDirectory(User user);

    void cleanAll() throws IOException;
}
