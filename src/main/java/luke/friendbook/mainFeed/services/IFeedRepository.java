package luke.friendbook.mainFeed.services;

import luke.friendbook.model.Chunk;
import luke.friendbook.model.Page;
import luke.friendbook.mainFeed.model.FeedModelDto;
import luke.friendbook.model.Repository;
import luke.friendbook.mainFeed.model.FeedModel;

public interface IFeedRepository extends Repository<FeedModel> {

    Page<FeedModelDto> findPage(int pageNumber, int pageSize);

    Chunk<FeedModelDto> findChunk(int limit, long offset);

    void deleteFeed(FeedModel feedModel);
}