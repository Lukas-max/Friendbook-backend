package luke.friendbook.mainFeed.services;

import luke.friendbook.storage.model.DirectoryType;
import luke.friendbook.storage.model.FileData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFeedStorage {

    void init() throws IOException;

    void cleanAll() throws IOException;

    FileData[] findFileData(Long feedId) throws IOException;

    byte[] download(String feedId, String fileName, DirectoryType type);

    void saveFeedFiles(MultipartFile[] files, Long feedNumber);

    void saveFeedFilesPlusCompressed(MultipartFile[] files, MultipartFile[] images, Long feedNumber);

    void deleteFeedFiles(String feedId);

    void deleteFeedImages(String feedId);
}
