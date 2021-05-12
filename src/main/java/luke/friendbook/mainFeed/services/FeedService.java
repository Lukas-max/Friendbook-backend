package luke.friendbook.mainFeed.services;

import luke.friendbook.model.Chunk;
import luke.friendbook.account.model.User;
import luke.friendbook.exception.model.NotFoundException;
import luke.friendbook.exception.model.UserUnauthorizedException;
import luke.friendbook.mainFeed.model.FeedModel;
import luke.friendbook.mainFeed.model.FeedModelDto;
import luke.friendbook.security.model.SecurityContextUser;
import luke.friendbook.storage.model.DirectoryType;
import luke.friendbook.storage.model.FileData;
import luke.friendbook.utilities.Utils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Service
public class FeedService implements IFeedService {

    private final IFeedRepository feedRepository;
    private final IFeedStorage feedStorage;

    public FeedService(IFeedRepository feedRepository, IFeedStorage feedStorage) {
        this.feedRepository = feedRepository;
        this.feedStorage = feedStorage;
    }

    @Override
    public List<FeedModelDto> findFeedData() throws IOException {
        List<FeedModel> feedModels = feedRepository.findAll();
        List<FeedModelDto> feedModelDtoList = Utils.returnFeedModelDto(feedModels);

        for (FeedModelDto feed : feedModelDtoList) {
            if (feed.getFiles()) {
                Long feedId = feed.getFeedId();
                FileData[] fileData = feedStorage.findFileData(feedId);
                feed.setFileData(fileData);
            }
        }
        return feedModelDtoList;
    }

    @Override
    public Chunk<FeedModelDto> findFeedChunkData(int limit, long offset) throws IOException {
        Chunk<FeedModelDto> feedModelDtoChunk = feedRepository.findChunk(limit, offset);

        for (FeedModelDto feed : feedModelDtoChunk.getContent()) {
            if (feed.getFiles()) {
                Long feedId = feed.getFeedId();
                FileData[] fileData = feedStorage.findFileData(feedId);
                feed.setFileData(fileData);
            }
        }
        return feedModelDtoChunk;
    }

    public byte[] download(String feedId, String fileName, DirectoryType directoryType) {
        return feedStorage.download(feedId, fileName, directoryType);
    }

    @Override
    public FeedModelDto saveTextFeed(String text) {
        User user = getAuthenticatedUser();
        FeedModel userFeed = FeedModel.builder()
                .text(text)
                .user(user)
                .files(false)
                .images(false)
                .feedTimestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        FeedModel savedFeed = feedRepository.save(userFeed);
        return new FeedModelDto(savedFeed);
    }

    @Override
    public FeedModelDto saveFeedWithFiles(MultipartFile[] files, String text) throws IOException {
        User user = getAuthenticatedUser();
        FeedModel userFeed = FeedModel.builder()
                .text(text)
                .user(user)
                .files(true)
                .images(false)
                .feedTimestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        FeedModel persistedFeed = feedRepository.save(userFeed);
        feedStorage.saveFeedFiles(files, persistedFeed.getId());
        FileData[] fileData = feedStorage.findFileData(persistedFeed.getId());
        return new FeedModelDto(persistedFeed, fileData);
    }

    @Override
    public FeedModelDto saveFeedWithFilesPlusCompressed(MultipartFile[] files, MultipartFile[] images, String text) throws IOException {
        User user = getAuthenticatedUser();
        FeedModel userFeed = FeedModel.builder()
                .text(text)
                .user(user)
                .files(true)
                .images(true)
                .feedTimestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        FeedModel persistedFeed = feedRepository.save(userFeed);
        feedStorage.saveFeedFilesPlusCompressed(files, images, persistedFeed.getId());
        FileData[] fileData = feedStorage.findFileData(persistedFeed.getId());
        return new FeedModelDto(persistedFeed, fileData);
    }

    @Override
    public void deleteFeed(String feedId) {
        FeedModel feed = feedRepository.findById(Long.valueOf(feedId))
                .orElseThrow(() -> new NotFoundException("Nie ma takiego zapisu w bazie by go usunąć"));

        SecurityContextUser user = Utils.getAuthenticatedUser();
        if (!user.getUser().getUserUUID().equals(feed.getUser().getUserUUID()))
            throw new UserUnauthorizedException("Nie masz dostępu by usunąć ten wpis");

        feedRepository.deleteFeed(feed);
        if (feed.getFiles())
            feedStorage.deleteFeedFiles(feedId);

        if (feed.getImages())
            feedStorage.deleteFeedImages(feedId);
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextUser contextUser = (SecurityContextUser) auth.getPrincipal();

        if (contextUser == null)
            throw new UserUnauthorizedException("Brak dostępu. Brak uwierzytelnionego użytkownika.");

        return contextUser.getUser();
    }
}
