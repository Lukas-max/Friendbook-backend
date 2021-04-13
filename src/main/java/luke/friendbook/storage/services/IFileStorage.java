package luke.friendbook.storage.services;

import luke.friendbook.account.model.User;
import luke.friendbook.storage.model.FileData;

import java.io.File;
import java.io.IOException;

public interface IFileStorage {

    void init() throws IOException;

    byte[] download(String userUUID, String directory, String fileName);

    File[] findUserDirectories(String userUUID);

    FileData[] findFiles(String userUUID, String directory) throws IOException;

    void createRegisteredUserStorageDirectory(User user);

    void cleanAll() throws IOException;
}
