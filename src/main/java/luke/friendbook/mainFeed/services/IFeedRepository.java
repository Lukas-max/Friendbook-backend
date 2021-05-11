package luke.friendbook.mainFeed.services;

import luke.friendbook.mainFeed.model.FeedModel;
import luke.friendbook.mainFeed.model.FeedModelDto;
import luke.friendbook.model.Chunk;
import luke.friendbook.model.Page;
import luke.friendbook.model.Repository;

import java.util.List;

public interface IFeedRepository extends Repository<FeedModel> {

    List<FeedModel> findAllByUser(Long userId);

    Page<FeedModelDto> findPage(int pageNumber, int pageSize);

    Chunk<FeedModelDto> findChunk(int limit, long offset);

    void deleteFeed(FeedModel feedModel);
}
