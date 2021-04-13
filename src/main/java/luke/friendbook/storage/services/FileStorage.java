package luke.friendbook.storage.services;

import luke.friendbook.account.model.User;
import luke.friendbook.account.services.IUserRepository;
import luke.friendbook.exception.DirectoryCreationFailException;
import luke.friendbook.exception.FileNotFoundException;
import luke.friendbook.exception.FileUnreadableException;
import luke.friendbook.exception.UserNotFoundException;
import luke.friendbook.security.model.SecurityContextUser;
import luke.friendbook.storage.FileController;
import luke.friendbook.storage.model.FileData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

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
            Path userPath = Paths.get(user.getUserId().toString());
            Files.createDirectory(mainDir.resolve(userPath));
            Files.createDirectory(mainDir.resolve(userPath).resolve(storageDir));
            Files.createDirectory(mainDir.resolve(userPath).resolve(otherDir));
            ++userDirCreated;
        }
        log.info("Created " + userDirCreated + " user directories in folder: " + mainDir);
    }

    @Override
    public byte[] download(String id, String directory, String fileName) {
        Path file = mainDir
                .resolve(id)
                .resolve(directory)
                .resolve(fileName);

        if (!Files.isRegularFile(file) || !Files.isReadable(file))
            throw new FileNotFoundException("Nie można znaleźć pliku " + fileName);

        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileUnreadableException("Nie da się odczytać pliku " + fileName);
        }
    }

    @Override
    public File[] findUserDirectories(String userUUID) {
        String userDir = getDirectory(userUUID);
        return new File(mainDir.resolve(userDir).toUri())
                .listFiles(File::isDirectory);
    }

    @Override
    public FileData[] findFiles(String userUUID, String directory) throws IOException {
        String userId = resolveUserFolder(userUUID);
        Path searchedDirectory = mainDir
                .resolve(userId)
                .resolve(directory);

        return Files.list(searchedDirectory)
                .map(file -> {
                    String fileUrl = createFileUrl("downloadFile", userId, directory, file.getFileName());
                    FileData fileData = null;

                    try {
                        fileData = new FileData(
                                file.getFileName().toString(),
                                fileUrl,
                                createFileTypeFromMimeType(Files.probeContentType(file)),
                                file.toFile().length());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return fileData;
                }).toArray(FileData[]::new);
    }

    private String getDirectory(String userUUID) {
        return userUUID == null ? resolveAuthenticatedUserFolder() : resolveUserFolder(userUUID);
    }

    private String resolveAuthenticatedUserFolder() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextUser contextUser = (SecurityContextUser) auth.getPrincipal();
        return contextUser.getUser().getUserId().toString();
    }

    /**
     * !!!!!!
     * Ta metoda będzie do usunięcia kiedy szukanie po katologach będzie tylko po UUID. Nie potrzebny będzie
     * userId.
     */
    private String resolveUserFolder(String userUUID) {
        User user = userRepository.findByUuid(userUUID).orElseThrow(() ->
                new UserNotFoundException("Nie znaleziono użytkownika po przesłanym numerze identyfikacyjnym"));

        return user.getUserId().toString();
    }

    @Override
    public void createRegisteredUserStorageDirectory(User user) {
        try {
            Path newUserPath = Paths.get(user.getUserId().toString());
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

    private String createFileUrl(String methodName, String user, String dir, Path file) {
        return MvcUriComponentsBuilder
                .fromMethodName(FileController.class, methodName, user, dir, file.getFileName().toString())
                .build().toString();
    }

    private String createFileTypeFromMimeType(String mimeType) {
        return mimeType.split("/")[0];
    }
}








