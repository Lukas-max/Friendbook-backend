package luke.friendbook.mainFeed.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFeedStorage {

    void init() throws IOException;

    void cleanAll() throws IOException;

    int saveFeedFiles(MultipartFile[] files, Long feedNumber);

    int saveFeedFilesPlusCompressed(MultipartFile[] files, MultipartFile[] images, Long feedNumber);
}
