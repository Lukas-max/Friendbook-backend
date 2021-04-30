package luke.friendbook.mainFeed.services;

import luke.friendbook.mainFeed.model.FeedComment;
import luke.friendbook.model.Chunk;
import org.springframework.stereotype.Service;

@Service
public class FeedCommentService implements IFeedCommentService{

    private final IFeedCommentRepository feedCommentRepository;

    public FeedCommentService(IFeedCommentRepository feedCommentRepository) {
        this.feedCommentRepository = feedCommentRepository;
    }

    @Override
    public Chunk<FeedComment> findFeedCommentChunk(long feedId, int limit, long offset) {
        return feedCommentRepository.findChunk(feedId, limit, offset);
    }

    @Override
    public void saveComment(FeedComment feedComment) {
        feedCommentRepository.save(feedComment);
    }
}
