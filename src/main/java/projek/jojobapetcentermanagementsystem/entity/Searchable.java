package projek.jojobapetcentermanagementsystem.entity;

/** Objek yang bisa dicocokkan dengan kata kunci pencarian. */
public interface Searchable {

    /** @return true bila objek ini cocok dengan kata kunci (tidak peka huruf besar/kecil). */
    boolean matches(String keyword);
}
