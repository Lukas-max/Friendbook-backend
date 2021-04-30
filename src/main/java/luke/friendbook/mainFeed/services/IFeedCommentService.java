package luke.friendbook.mainFeed.services;

import luke.friendbook.mainFeed.model.FeedComment;
import luke.friendbook.model.Chunk;

public interface IFeedCommentService {

    Chunk<FeedComment> findFeedCommentChunk(long feedId, int limit, long offset);

    void saveComment(FeedComment feedComment);
}
