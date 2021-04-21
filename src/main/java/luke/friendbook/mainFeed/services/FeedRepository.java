package luke.friendbook.mainFeed.services;

import luke.friendbook.mainFeed.model.FeedModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class FeedRepository implements IFeedRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<FeedModel> findAll() {
        return null;
    }

    @Override
    public Optional<FeedModel> findById(Long id) {
        return Optional.empty();
    }

    @Override
    @Transactional
    public FeedModel save(FeedModel feedModel) {
        return entityManager.merge(feedModel);
    }

    @Override
    @Transactional
    public Iterable<FeedModel> saveAll(Iterable<FeedModel> t) {
        return null;
    }

    @Override
    @Transactional
    public FeedModel update(FeedModel feedModel) {
        return null;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return false;
    }
}
