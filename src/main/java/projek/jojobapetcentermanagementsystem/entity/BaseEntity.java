package projek.jojobapetcentermanagementsystem.entity;

/**
 * Induk semua entity: menyimpan primary key (id) yang sama-sama dimiliki
 * setiap tabel. Abstract, jadi tidak bisa dibuat langsung (new BaseEntity()).
 */
public abstract class BaseEntity {

    private int id;

    protected BaseEntity() {
    }

    protected BaseEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
