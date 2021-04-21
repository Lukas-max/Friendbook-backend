package luke.friendbook.mainFeed.services;

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
public class FeedStorage implements IFeedStorage{

    private final Path mainFeedDir = Paths.get("main-feed");
    private final Path feedDir = Paths.get("feed");
    private final Path imagesDir = Paths.get("images");
    private final Logger log = LoggerFactory.getLogger(FeedStorage.class);

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

    @Override
    public int saveFeedFiles(MultipartFile[] files, Long feedNumber){
        int savedFiles = 0;
        String number = feedNumber.toString();
        Path feedPath = mainFeedDir.resolve(feedDir).resolve(Paths.get(number));

        try {
            Files.createDirectory(feedPath);

            for (MultipartFile file : files){
                Files.copy(file.getInputStream(),feedPath.resolve(file.getOriginalFilename()));
                ++savedFiles;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return savedFiles;
    }

    @Override
    public int saveFeedFilesPlusCompressed(MultipartFile[] files, MultipartFile[] images, Long feedNumber) {
        int savedFiles = 0;
        String number = feedNumber.toString();
        Path feedPath = mainFeedDir.resolve(feedDir).resolve(Paths.get(number));
        Path imagePath = mainFeedDir.resolve(imagesDir).resolve(Paths.get(number));

        try{
            Files.createDirectory(feedPath);
            Files.createDirectory(imagePath);

            for (MultipartFile file : files) {
                Files.copy(file.getInputStream(), feedPath.resolve(file.getOriginalFilename()));
                ++savedFiles;
            }

            for (MultipartFile imageFile : images) {
                Files.copy(imageFile.getInputStream(), imagePath.resolve(imageFile.getOriginalFilename()));
            }
        }catch (IOException e){
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
        return savedFiles;
    }
}
















