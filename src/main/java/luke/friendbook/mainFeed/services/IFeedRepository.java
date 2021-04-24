package luke.friendbook.mainFeed.services;

import luke.friendbook.utilities.Repository;
import luke.friendbook.mainFeed.model.FeedModel;

public interface IFeedRepository extends Repository<FeedModel> {

    void deleteFeed(FeedModel feedModel);
}
