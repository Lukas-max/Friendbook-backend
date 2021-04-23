package luke.friendbook.mainFeed.services;

import luke.friendbook.mainFeed.model.FeedModelDto;
import luke.friendbook.storage.model.DirectoryType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IFeedService {

    List<FeedModelDto> findFeedData() throws IOException;

    byte[] download(String feedId, String fileName, DirectoryType directoryType);

    void saveTextFeed(String text);

    int saveFeedWithFiles(MultipartFile[] files, String text);

    int saveFeedWithFilesPlusCompressed(MultipartFile[] files, MultipartFile[] images, String text);
}
