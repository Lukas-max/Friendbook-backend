package luke.friendbook.storage.services;

import luke.friendbook.account.model.User;
import luke.friendbook.account.services.IUserRepository;
import luke.friendbook.exception.DirectoryCreationFailException;
import luke.friendbook.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileStorage implements IFileStorage {

    private final Path mainDir = Paths.get("users");
    private final Path storageDir = Paths.get("pliki");
    private final Path otherDir = Paths.get("inne");
    private final IUserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(FileStorage.class);

    public FileStorage(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public void init() throws IOException {
        int userDirCreated = 0;
        Files.createDirectory(mainDir);

        String dir = Paths.get("").toAbsolutePath().toString();
        System.out.println("ABSOLUTE: " + dir);

        List<User> users = userRepository.findAll();
        for (User user : users) {
            Path userPath = Paths.get(user.getUserId() + "-" + user.getEmail());
            Files.createDirectory(mainDir.resolve(userPath));
            Files.createDirectory(mainDir.resolve(userPath).resolve(storageDir));
            Files.createDirectory(mainDir.resolve(userPath).resolve(otherDir));
            ++userDirCreated;
        }
        log.info("Created " + userDirCreated + " user directories in folder: " + mainDir);
    }

    @Override
    public File[] findUserDirectories(String userUUID) {
        String userDir = userUUID == null ? resolveAuthenticatedUserFolder() : resolveUserFolder(userUUID);
        return new File(mainDir.resolve(userDir).toUri())
                .listFiles(File::isDirectory);
    }

    private String resolveAuthenticatedUserFolder() {
        System.out.println("AUTHENTICATED");
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email).orElseThrow(() ->
                    new UserNotFoundException("nie znaleziono: FileStorage"));
            return user.getUserId() + "-" + user.getEmail();
    }

    private String resolveUserFolder(String userUUID) {
        System.out.println("NOT AUTHENTICATED");
        User user = userRepository.findByUuid(userUUID).orElseThrow(() ->
                new UserNotFoundException("Nie znaleziono użytkownika po przesłanym numerze identyfikacyjnym"));

        return user.getUserId() + "-" + user.getEmail();
    }

    @Override
    public void createRegisteredUserStorageDirectory(User user) {
        try {
            Path newUserPath = Paths.get(user.getUserId() + "-" + user.getEmail());
            Files.createDirectory(mainDir.resolve(newUserPath));
            Files.createDirectory(mainDir.resolve(newUserPath).resolve(storageDir));
            Files.createDirectory(mainDir.resolve(newUserPath).resolve(otherDir));
        } catch (IOException e) {
            e.printStackTrace();
            throw new DirectoryCreationFailException("Nie mogłem utworzyć folderu dla plików. " +
                    "Skontaktuj się z administratorem.");
        }
        log.info("Created directory " + user.getEmail() + " for new user: " + user.getUsername());
    }

    @Override
    public void cleanAll() throws IOException {
        FileSystemUtils.deleteRecursively(mainDir);
        log.info("FileStorage - cleaned all files and directories.");
    }
}








