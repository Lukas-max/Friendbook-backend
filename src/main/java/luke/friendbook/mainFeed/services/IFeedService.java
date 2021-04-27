package luke.friendbook.mainFeed.services;

import luke.friendbook.Chunk;
import luke.friendbook.mainFeed.model.FeedModelDto;
import luke.friendbook.storage.model.DirectoryType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IFeedService {

    List<FeedModelDto> findFeedData() throws IOException;

    Chunk<FeedModelDto> findFeedChunkData(int limit, long offset) throws IOException;

    byte[] download(String feedId, String fileName, DirectoryType directoryType);

    FeedModelDto saveTextFeed(String text);

    FeedModelDto saveFeedWithFiles(MultipartFile[] files, String text) throws IOException;

    FeedModelDto saveFeedWithFilesPlusCompressed(MultipartFile[] files, MultipartFile[] images, String text) throws IOException;

    void deleteFeed(String feedId);
}
