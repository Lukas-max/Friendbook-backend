package luke.friendbook;

import java.util.Optional;

public interface Repository<T> {

    Optional<T> findById(Long id);

    T save(T t);

    Iterable<T> saveAll(Iterable<T> t);

    T update(T t);

    boolean deleteById(Long id);
}
