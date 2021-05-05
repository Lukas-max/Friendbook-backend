package luke.friendbook.mainFeed.services;

import luke.friendbook.mainFeed.model.FeedComment;
import luke.friendbook.model.Chunk;

public interface IFeedCommentService {

    Chunk<FeedComment> findCommentChunk(long feedId, int limit, long offset);

    FeedComment saveComment(FeedComment feedComment);

    void deleteCommentById(Long id);

    void deleteAllCommentsByFeed(Long feedId);
}
