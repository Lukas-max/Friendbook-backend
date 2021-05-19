package luke.friendbook.mainFeed.services;

import luke.friendbook.utilities.Utils;
import luke.friendbook.exception.model.FileUnreadableException;
import luke.friendbook.exception.model.NotFoundException;
import luke.friendbook.storage.model.DirectoryType;
import luke.friendbook.storage.model.FileData;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FeedStorage implements IFeedStorage {

    private final Path mainFeedDir = Paths.get("main-feed");
    private final Path feedDir = Paths.get("feed");
    private final Path imagesDir = Paths.get("images");
    private final Tika tika;
    private final Logger log = LoggerFactory.getLogger(FeedStorage.class);

    public FeedStorage(Tika tika) {
        this.tika = tika;
    }

    @Override
    public void init() throws IOException {
        cleanAll();

        Files.createDirectory(mainFeedDir);
        Files.createDirectory(mainFeedDir.resolve(feedDir));
        Files.createDirectory(mainFeedDir.resolve(imagesDir));
        log.info("Created main feed folder");
    }

    @Override
    public void cleanAll() throws IOException {
        FileSystemUtils.deleteRecursively(mainFeedDir);
    }

    public FileData[] findFileData(Long feedId) throws IOException {
        Path path = mainFeedDir.resolve(feedDir).resolve(feedId.toString());

        return Files.list(path)
                .map(file -> {
                    FileData fileData = null;

                    try {
                        String mimeType = tika.detect(file);
                        String fileType = Utils.createFileTypeFromMimeType(mimeType);

                        fileData = new FileData(
                                file.getFileName().toString(),
                                Utils.createFeedFileUrl("downloadFeedFile", feedId, file.getFileName().toString()),
                                mimeType,
                                fileType,
                                file.toFile().length());

                        if (fileType.equals("image")) {
                            String url = Utils.createFeedFileUrl("downloadFeedImageFile", feedId, file.getFileName().toString());
                            fileData.setImageUrl(url);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return fileData;
                }).toArray(FileData[]::new);
    }

    public byte[] download(String feedId, String fileName, DirectoryType type) {
        Path file;
        if (type.equals(DirectoryType.STANDARD_DIRECTORY)) {
            file = mainFeedDir
                    .resolve(feedDir)
                    .resolve(feedId)
                    .resolve(fileName);
        } else {
            file = mainFeedDir
                    .resolve(imagesDir)
                    .resolve(feedId)
                    .resolve(fileName);
        }

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
    public void saveFeedFiles(MultipartFile[] files, Long feedNumber) {
        String number = feedNumber.toString();
        Path feedPath = mainFeedDir.resolve(feedDir).resolve(Paths.get(number));

        try {
            Files.createDirectory(feedPath);

            for (MultipartFile file : files) {
                Files.copy(file.getInputStream(), feedPath.resolve(file.getOriginalFilename()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveFeedFilesPlusCompressed(MultipartFile[] files, MultipartFile[] images, Long feedNumber) {
        String number = feedNumber.toString();
        Path feedPath = mainFeedDir.resolve(feedDir).resolve(Paths.get(number));
        Path imagePath = mainFeedDir.resolve(imagesDir).resolve(Paths.get(number));

        try {
            Files.createDirectory(feedPath);
            Files.createDirectory(imagePath);

            for (MultipartFile file : files) {
                Files.copy(file.getInputStream(), feedPath.resolve(file.getOriginalFilename()));
            }

            for (MultipartFile imageFile : images) {
                Files.copy(imageFile.getInputStream(), imagePath.resolve(imageFile.getOriginalFilename()));
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFeedFiles(String feedId) {
        Path deleteFilesPath = mainFeedDir.resolve(feedDir).resolve(feedId);

        try {
            FileSystemUtils.deleteRecursively(deleteFilesPath);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFeedImages(String feedId) {
        Path deleteImagesPath = mainFeedDir.resolve(imagesDir).resolve(feedId);

        try {
            FileSystemUtils.deleteRecursively(deleteImagesPath);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
