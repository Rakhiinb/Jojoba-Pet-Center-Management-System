package projek.jojobapetcentermanagementsystem.repository;

import java.util.List;

/**
 * Kontrak CRUD dasar untuk repository.
 * save, update, dan delete mengembalikan boolean agar view tahu berhasil
 * atau tidak (mis. delete gagal karena data masih dipakai FOREIGN KEY).
 */
public interface CrudRepository<T> {

    List<T> findAll();

    /** @return objek yang dicari, atau null bila id tidak ada. */
    T findById(int id);

    boolean save(T entity);

    boolean update(T entity);

    boolean delete(int id);
}
