package luke.friendbook.storage.services;

import luke.friendbook.account.model.User;

import java.io.File;
import java.io.IOException;

public interface IFileStorage {

    void init() throws IOException;

    File[] findUserDirectories(String userUUID);

    void createRegisteredUserStorageDirectory(User user);

    void cleanAll() throws IOException;
}
