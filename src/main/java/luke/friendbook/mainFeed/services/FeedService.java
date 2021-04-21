package luke.friendbook.mainFeed.services;

import luke.friendbook.account.model.User;
import luke.friendbook.exception.UserUnauthorizedException;
import luke.friendbook.mainFeed.model.FeedModel;
import luke.friendbook.security.model.SecurityContextUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FeedService implements IFeedService{

    private final IFeedRepository feedRepository;
    private final IFeedStorage feedStorage;

    public FeedService(IFeedRepository feedRepository, IFeedStorage feedStorage) {
        this.feedRepository = feedRepository;
        this.feedStorage = feedStorage;
    }


    @Override
    public void saveTextFeed(String text) {
        User user = getAuthenticatedUser();
        FeedModel userFeed = FeedModel.builder()
                .text(text)
                .user(user)
                .files(false)
                .images(false)
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
}
