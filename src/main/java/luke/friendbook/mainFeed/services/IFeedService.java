package luke.friendbook.mainFeed.services;

import org.springframework.web.multipart.MultipartFile;

public interface IFeedService {

    void saveTextFeed(String text);

    int saveFeedWithFiles(MultipartFile[] files, String text);

    int saveFeedWithFilesPlusCompressed(MultipartFile[] files, MultipartFile[] images, String text);
}
