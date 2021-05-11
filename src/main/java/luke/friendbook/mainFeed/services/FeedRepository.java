package luke.friendbook.mainFeed.services;

import luke.friendbook.mainFeed.model.FeedModel;
import luke.friendbook.mainFeed.model.FeedModelDto;
import luke.friendbook.model.Chunk;
import luke.friendbook.model.Page;
import luke.friendbook.utilities.Utils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Repository
public class FeedRepository implements IFeedRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    @Transactional
    public List<FeedModel> findAll() {
        final String query = "SELECT f FROM FeedModel f";
        TypedQuery<FeedModel> feedTypedQuery = entityManager.createQuery(query, FeedModel.class);
        return feedTypedQuery.getResultList();
    }

    @Override
    @Transactional
    public List<FeedModel> findAllByUser(Long userId) {
        final String sql = "SELECT f FROM FeedModel f WHERE f.user.userId= ?1";
        TypedQuery<FeedModel> feedTypedQuery = entityManager.createQuery(sql, FeedModel.class)
                .setParameter(1, userId);

        return feedTypedQuery.getResultList();
    }

    @Override
    @Transactional
    public Page<FeedModelDto> findPage(int pageNumber, int pageSize) {
        Query countQuery = entityManager.createQuery("SELECT COUNT(f.id) FROM FeedModel f");
        long countResult = (long) countQuery.getSingleResult();
        int totalPages = (int) (countResult / pageSize);

        final String query = "SELECT f FROM FeedModel f ORDER BY f.id DESC";
        TypedQuery<FeedModel> feedTypedQuery = entityManager.createQuery(query, FeedModel.class)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize);

        List<FeedModel> feedModelList = feedTypedQuery.getResultList();
        List<FeedModelDto> feedModelDtoList = Utils.returnFeedModelDto(feedModelList);
        return new Page<>(countResult, totalPages, feedModelList.size(), pageNumber, feedModelDtoList);
    }

    @Override
    @Transactional
    public Chunk<FeedModelDto> findChunk(int limit, long offset) {
        final String query = "SELECT * FROM feed ORDER BY id DESC LIMIT ?1 OFFSET ?2";
        Query feedQuery = entityManager.createNativeQuery(query, FeedModel.class)
                .setParameter(1, limit)
                .setParameter(2, offset);

        @SuppressWarnings("unchecked")
        List<FeedModel> feedModelList = (List<FeedModel>) feedQuery.getResultList();
        List<FeedModelDto> feedModelDtoList = Utils.returnFeedModelDto(feedModelList);
        return new Chunk<>(limit, offset, feedModelDtoList);
    }

    @Override
    @Transactional
    public Optional<FeedModel> findById(Long id) {
        final String query = "SELECT f FROM FeedModel f WHERE f.id = ?1";
        TypedQuery<FeedModel> feedTypedQuery = entityManager.createQuery(query, FeedModel.class);
        feedTypedQuery.setParameter(1, id);
        FeedModel feed;

        try {
            feed = feedTypedQuery.getSingleResult();
        } catch (NoResultException e) {
            return Optional.empty();
        }
        return Optional.of(feed);
    }

    @Override
    @Transactional
    public FeedModel save(FeedModel feedModel) {
        return entityManager.merge(feedModel);
    }

    @Override
    @Transactional
    public Iterable<FeedModel> saveAll(Iterable<FeedModel> feeds) {
        feeds.forEach(this::save);
        return feeds;
    }

    @Override
    @Transactional
    public FeedModel update(FeedModel feedModel) {
        return null;
    }

    @Override
    @Transactional
    public boolean deleteById(Long feedId) {
        FeedModel feed = entityManager.find(FeedModel.class, feedId);
        if (feed != null) {
            entityManager.remove(feed);
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public void deleteFeed(FeedModel feedModel) {
        entityManager.remove(feedModel);
    }
}
