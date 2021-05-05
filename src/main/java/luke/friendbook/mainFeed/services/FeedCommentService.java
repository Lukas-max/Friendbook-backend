package luke.friendbook.mainFeed.services;

import luke.friendbook.exception.NotFoundException;
import luke.friendbook.exception.UserUnauthorizedException;
import luke.friendbook.mainFeed.model.FeedComment;
import luke.friendbook.model.Chunk;
import luke.friendbook.security.model.SecurityContextUser;
import luke.friendbook.utilities.Utils;
import org.springframework.stereotype.Service;

@Service
public class FeedCommentService implements IFeedCommentService {

    private final IFeedCommentRepository feedCommentRepository;

    public FeedCommentService(IFeedCommentRepository feedCommentRepository) {
        this.feedCommentRepository = feedCommentRepository;
    }

    @Override
    public Chunk<FeedComment> findCommentChunk(long feedId, int limit, long offset) {
        return feedCommentRepository.findChunk(feedId, limit, offset);
    }

    @Override
    public FeedComment saveComment(FeedComment feedComment) {
        return feedCommentRepository.save(feedComment);
    }

    @Override
    public void deleteCommentById(Long id) {
        FeedComment feedComment = feedCommentRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Nie znaleziono komentarza"));

        SecurityContextUser user = Utils.getAuthenticatedUser();
        if (user.getUser().getUserUUID().equals(feedComment.getUserUUID()))
            feedCommentRepository.deleteById(id);
        else
            throw new UserUnauthorizedException("Nie masz dostępu do usunięcia tego komentarza");
    }

    @Override
    public void deleteAllCommentsByFeed(Long feedId) {
        feedCommentRepository.deleteCommentsFromFeed(feedId);
    }
}
