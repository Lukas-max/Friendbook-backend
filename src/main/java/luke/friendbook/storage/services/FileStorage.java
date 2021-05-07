package luke.friendbook.storage.services;

import luke.friendbook.account.model.User;
import luke.friendbook.account.services.IUserRepository;
import luke.friendbook.exception.*;
import luke.friendbook.model.Chunk;
import luke.friendbook.security.model.SecurityContextUser;
import luke.friendbook.storage.model.DirectoryType;
import luke.friendbook.storage.model.FileData;
import luke.friendbook.storage.model.FileQuality;
import luke.friendbook.utilities.Utils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileStorage implements IFileStorage {

    private final Path imageDir = Path.of("images");
    private final Path mainDir = Path.of("users");
    private final Path profileDir = Path.of("profile-photo");

    private final Path storageDir = Path.of("pliki");
    private final Path otherDir = Path.of("inne");
    private final Path lowQualityProfile = Path.of("low");
    private final Path highQualityProfile = Path.of("high");

    private final IUserRepository userRepository;
    private final Tika tika;
    private final Logger log = LoggerFactory.getLogger(FileStorage.class);

    public FileStorage(IUserRepository userRepository, Tika tika) {
        this.userRepository = userRepository;
        this.tika = tika;
    }


    @Override
    public void init() throws IOException {
        cleanAll();  // this will wipe out all folders and files on init!

        int userDirCreated = 0;
        Files.createDirectory(mainDir);
        Files.createDirectory(imageDir);
        Files.createDirectory(profileDir);

        String dir = Paths.get("").toAbsolutePath().toString();
        System.out.println("ABSOLUTE: " + dir);

        List<User> users = userRepository.findAll();
        for (User user : users) {
            Path userPath = Path.of(user.getUserId().toString());
            Files.createDirectory(mainDir.resolve(userPath));
            Files.createDirectory(mainDir.resolve(userPath).resolve(storageDir));
            Files.createDirectory(mainDir.resolve(userPath).resolve(otherDir));

            Files.createDirectory(imageDir.resolve(userPath));
            Files.createDirectory(imageDir.resolve(userPath).resolve(storageDir));
            Files.createDirectory(imageDir.resolve(userPath).resolve(otherDir));

            Path userProfilePath = Path.of(user.getUserUUID());
            Files.createDirectory(profileDir.resolve(userProfilePath));
            Files.createDirectory(profileDir.resolve(userProfilePath).resolve(lowQualityProfile));
            Files.createDirectory(profileDir.resolve(userProfilePath).resolve(highQualityProfile));

            ++userDirCreated;
        }
        log.info("Created " + userDirCreated + " user directories in folder: " + mainDir);
    }

    @Override
    public byte[] download(String id, String directory, String fileName, DirectoryType dirType) {
        Path rootDir = dirType == DirectoryType.STANDARD_DIRECTORY ? mainDir : imageDir;
        Path file = rootDir
                .resolve(id)
                .resolve(directory)
                .resolve(fileName);

        if (!Files.isRegularFile(file) || !Files.isReadable(file))
            throw new NotFoundException("Nie można znaleźć pliku " + fileName);

        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileUnreadableException("Nie da się odczytać pliku " + fileName);
        }
    }

    @Override
    public byte[] downloadProfilePhoto(String userUUID, FileQuality quality) throws IOException {
        Path qualityDir = quality == FileQuality.HIGH ? highQualityProfile : lowQualityProfile;
        Path path = profileDir
                .resolve(userUUID)
                .resolve(qualityDir);

        String fileName;
        Path[] paths = Files
                .list(path)
                .toArray(Path[]::new);

        if (paths.length == 0)
            return new byte[]{};

        fileName = paths[0].toFile().getName();
        Path profilePhoto = profileDir
                .resolve(userUUID)
                .resolve(qualityDir)
                .resolve(fileName);

        if (!Files.isRegularFile(profilePhoto) || !Files.isReadable(profilePhoto))
            throw new NotFoundException("Nie można znaleźć pliku " + fileName);

        return Files.readAllBytes(profilePhoto);
    }

    @Override
    public File[] findDirectories(String userUUID) {
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
                    FileData fileData = null;

                    try {
                        String mimeType = tika.detect(file);
                        String fileType = Utils.createFileTypeFromMimeType(mimeType);

                        fileData = new FileData(
                                file.getFileName().toString(),
                                Utils.createStorageFileUrl("downloadFile", userId, directory, file.getFileName().toString()),
                                mimeType,
                                fileType,
                                file.toFile().length());

                        if (fileType.equals("image")) {
                            String url = Utils.createStorageFileUrl("downloadImage", userId, directory, file.getFileName().toString());
                            fileData.setImageUrl(url);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return fileData;
                }).toArray(FileData[]::new);
    }

    @Override
    public Chunk<FileData> findFilesChunk(String userUUID, String directory, int limit, int offset) throws IOException {
        String userId = resolveUserFolder(userUUID);
        Path searchedDirectory = mainDir
                .resolve(userId)
                .resolve(directory);

        File file = new File(searchedDirectory.toString());
        File[] files = file.listFiles();
        List<FileData> fileDataList = new ArrayList<>();

        for (int i = offset; i < (offset + limit) && i < files.length; i++) {
            String mimeType = tika.detect(files[i]);
            String fileType = Utils.createFileTypeFromMimeType(mimeType);

            FileData fileData = new FileData(
                    files[i].getName(),
                    Utils.createStorageFileUrl("downloadFile", userId, directory, files[i].getName()),
                    mimeType,
                    fileType,
                    files[i].length());

            if (fileType.equals("image")) {
                String url = Utils.createStorageFileUrl("downloadImage", userId, directory, files[i].getName());
                fileData.setImageUrl(url);
            }
            fileDataList.add(fileData);
        }
        return new Chunk<>(limit, offset, fileDataList);
    }

    @Override
    public void changeProfilePhoto(MultipartFile file) {
        deleteProfilePhoto();
        saveProfilePhoto(file);
    }

    @Override
    public void deleteProfilePhoto() {
        String userUUID = Utils.getAuthenticatedUser().getUser().getUserUUID();
        Path deletePath = profileDir
                .resolve(userUUID)
                .resolve(lowQualityProfile);
        File dir = new File(deletePath.toUri());

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
        }
    }

    private void saveProfilePhoto(MultipartFile file) {
        String userUUID = Utils.getAuthenticatedUser().getUser().getUserUUID();
        Path path = profileDir
                .resolve(userUUID)
                .resolve(lowQualityProfile);

        try {
            Files.copy(file.getInputStream(), path.resolve(file.getOriginalFilename()));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
            throw new FileNotStoredException(e.getLocalizedMessage());
        }
    }

    private String getDirectory(String userUUID) {
        return userUUID == null ? resolveAuthenticatedUserFolder() : resolveUserFolder(userUUID);
    }

    private String resolveAuthenticatedUserFolder() {
        SecurityContextUser contextUser = Utils.getAuthenticatedUser();

        if (contextUser == null)
            throw new UserUnauthorizedException("Brak dostępu. Brak uwierzytelnionego użytkownika.");

        return contextUser.getUser().getUserId().toString();
    }

    /**
     * !!!!!!
     * Ta metoda będzie do usunięcia kiedy szukanie po katologach będzie tylko po UUID. Nie potrzebny będzie
     * userId.
     */
    private String resolveUserFolder(String userUUID) {
        User user = userRepository.findByUuid(userUUID).orElseThrow(() ->
                new NotFoundException("Nie znaleziono użytkownika po przesłanym numerze identyfikacyjnym"));

        return user.getUserId().toString();
    }

    /**
     * Saves files to passed directory. User directory however is taken from Authentication context, UserDetails object.
     * So, saving files can be done only to directories of authenticated user in current request.
     */
    @Override
    public int save(MultipartFile[] files, String directory, DirectoryType dirType) {
        int savedFiles = 0;
        String id = resolveAuthenticatedUserFolder();
        Path filesDirectory = mainDir.resolve(id).resolve(directory);
        Path imageDirectory = imageDir.resolve(id).resolve(directory);

        for (MultipartFile file : files) {
            try {
                if (dirType == DirectoryType.STANDARD_DIRECTORY)
                    Files.copy(file.getInputStream(), filesDirectory.resolve(file.getOriginalFilename()));
                if (dirType == DirectoryType.IMAGE_DIRECTORY)
                    Files.copy(file.getInputStream(), imageDirectory.resolve(file.getOriginalFilename()));
            } catch (FileAlreadyExistsException e) {
                throw new FileExistsException("Plik " + file.getOriginalFilename() + " już istnieje. " +
                        "Przerwano zapisywanie plików.");
            } catch (IOException e) {
                log.error(e.getLocalizedMessage());
                e.printStackTrace();
                throw new FileNotStoredException(e.getLocalizedMessage());
            }
            ++savedFiles;
        }

        updateUserStorageSize();
        return savedFiles;
    }

    @Override
    public void createFolder(String directory) {
        String id = resolveAuthenticatedUserFolder();
        Path folderPath = mainDir.resolve(id).resolve(directory);
        Path imageFolderPath = imageDir.resolve(id).resolve(directory);

        try {
            Files.createDirectory(folderPath);
            Files.createDirectory(imageFolderPath);
        } catch (IOException e) {
            throw new FolderStorageException("Nie udało się stworzyć katologu.");
        }
    }

    @Override
    public boolean deleteFolder(String directory) {
        String id = resolveAuthenticatedUserFolder();
        Path deletePath = mainDir.resolve(id).resolve(directory);
        Path deleteImageFolderPath = imageDir.resolve(id).resolve(directory);

        if (!Files.exists(deletePath) || !Files.exists(deleteImageFolderPath))
            return false;

        try {
            FileSystemUtils.deleteRecursively(deletePath);
            FileSystemUtils.deleteRecursively(deleteImageFolderPath);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            return false;
        }

        updateUserStorageSize();
        return true;
    }

    @Override
    public void deleteFile(String directory, String fileName) {
        String id = resolveAuthenticatedUserFolder();
        Path deleteFilePath = mainDir.resolve(id).resolve(directory).resolve(fileName);
        Path deleteImageFilePath = imageDir.resolve(id).resolve(directory).resolve(fileName);

        try {
            boolean result = Files.deleteIfExists(deleteFilePath);
            Files.deleteIfExists(deleteImageFilePath);

            if (!result)
                throw new NotFoundException("Nie mogłem znaleźć pliku " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateUserStorageSize();
    }

    @Override
    public void createRegisteredUserStorageDirectory(User user) {
        try {
            Path newUserPath = Path.of(user.getUserId().toString());
            Files.createDirectory(mainDir.resolve(newUserPath));
            Files.createDirectory(mainDir.resolve(newUserPath).resolve(storageDir));
            Files.createDirectory(mainDir.resolve(newUserPath).resolve(otherDir));

            Files.createDirectory(imageDir.resolve(newUserPath));
            Files.createDirectory(imageDir.resolve(newUserPath).resolve(storageDir));
            Files.createDirectory(imageDir.resolve(newUserPath).resolve(otherDir));

            Path userProfilePath = Path.of(user.getUserUUID());
            Files.createDirectory(profileDir.resolve(userProfilePath));
            Files.createDirectory(profileDir.resolve(userProfilePath).resolve(lowQualityProfile));
            Files.createDirectory(profileDir.resolve(userProfilePath).resolve(highQualityProfile));
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
        FileSystemUtils.deleteRecursively(imageDir);
        FileSystemUtils.deleteRecursively(profileDir);
        log.info("FileStorage - cleaned all files and directories.");
    }

    @Override
    public void checkStorageSpace() {
        float megaByte = Utils.getAuthenticatedUser().getUser().getStorageSize();
        if (megaByte > 100)
            throw new ExceededStorageException("Przekroczono dopuszczalną ilość wolnego miejsca");
    }

    private void updateUserStorageSize(){
        String userUUID = Utils.getAuthenticatedUser().getUser().getUserUUID();
        Long userId = Utils.getAuthenticatedUser().getUser().getUserId();
        float size = getUserStorageSize(userId);
        userRepository.patchStorageSize(userUUID, size);
    }

    private float getUserStorageSize(Long id) {
        Path path = mainDir.resolve(id.toString());
        long size = 0;

        try (Stream<Path> fileWalk = Files.walk(path)) {
            size = fileWalk
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .mapToLong(File::length)
                    .sum();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return size/1_000_000f;
    }
}








