package luke.friendbook.mainFeed.services;

import luke.friendbook.account.model.User;
import luke.friendbook.exception.UserUnauthorizedException;
import luke.friendbook.mainFeed.model.FeedModel;
import luke.friendbook.mainFeed.model.FeedModelDto;
import luke.friendbook.security.model.SecurityContextUser;
import luke.friendbook.storage.model.DirectoryType;
import luke.friendbook.storage.model.FileData;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedService implements IFeedService {

    private final IFeedRepository feedRepository;
    private final IFeedStorage feedStorage;

    public FeedService(IFeedRepository feedRepository, IFeedStorage feedStorage) {
        this.feedRepository = feedRepository;
        this.feedStorage = feedStorage;
    }


    public List<FeedModelDto> findFeedData() throws IOException {
        List<FeedModel> feedModels = feedRepository.findAll();
        List<FeedModelDto> feedModelDtoList = returnFeedModelDto(feedModels);

        for (FeedModelDto feed : feedModelDtoList) {
            if (feed.getFiles()) {
                Long feedId = feed.getId();
                FileData[] fileData = feedStorage.findFileData(feedId);
                feed.setFileData(fileData);
            }
        }
        return feedModelDtoList;
    }

    public byte[] download(String feedId, String fileName, DirectoryType directoryType) {
        return feedStorage.download(feedId, fileName, directoryType);
    }

    @Override
    public void saveTextFeed(String text) {
        User user = getAuthenticatedUser();
        FeedModel userFeed = FeedModel.builder()
                .text(text)
                .user(user)
                .files(false)
                .images(false)
                .feedTimestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        feedRepository.save(userFeed);
    }

    @Override
    public int saveFeedWithFiles(MultipartFile[] files, String text) {
        User user = getAuthenticatedUser();
        FeedModel userFeed = FeedModel.builder()
                .text(text)
                .user(user)
                .files(true)
                .images(false)
                .feedTimestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        FeedModel persistedFeed = feedRepository.save(userFeed);
        return feedStorage.saveFeedFiles(files, persistedFeed.getId());
    }

    @Override
    public int saveFeedWithFilesPlusCompressed(MultipartFile[] files, MultipartFile[] images, String text) {
        User user = getAuthenticatedUser();
        FeedModel userFeed = FeedModel.builder()
                .text(text)
                .user(user)
                .files(true)
                .images(true)
                .feedTimestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        FeedModel persistedFeed = feedRepository.save(userFeed);
        return feedStorage.saveFeedFilesPlusCompressed(files, images, persistedFeed.getId());
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextUser contextUser = (SecurityContextUser) auth.getPrincipal();

        if (contextUser == null)
            throw new UserUnauthorizedException("Brak dostępu. Brak uwierzytelnionego użytkownika.");

        return contextUser.getUser();
    }

    private List<FeedModelDto> returnFeedModelDto(List<FeedModel> feedModels) {
        return feedModels.stream()
                .map(model -> {
                    return new FeedModelDto(
                            model.getId(),
                            model.getText(),
                            model.getFiles(),
                            model.getImages(),
                            model.getFeedTimestamp(),
                            model.getUser().getUsername(),
                            model.getUser().getUserUUID()
                    );
                }).collect(Collectors.toList());
    }
}
