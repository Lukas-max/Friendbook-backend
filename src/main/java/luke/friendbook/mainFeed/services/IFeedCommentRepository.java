package luke.friendbook.mainFeed.services;

import luke.friendbook.mainFeed.model.FeedComment;
import luke.friendbook.model.Chunk;
import luke.friendbook.model.Repository;

public interface IFeedCommentRepository extends Repository<FeedComment> {

    Chunk<FeedComment> findChunk(long feedId, int limit, long offset);

    void deleteCommentsFromFeed(Long feedId);

    void deleteCommentsByUser(String userUUID);
}
