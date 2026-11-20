package projek.jojobapetcentermanagementsystem.entity;

/** Satu baris tabel customers (pemilik hewan). */
public class Customer extends BaseEntity implements Searchable {

    private String name;
    private String phone;
    private String address;

    public Customer() {
    }

    /** Constructor tanpa id: untuk data baru yang belum disimpan. */
    public Customer(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    /** Constructor dengan id: untuk data yang dibaca dari database. */
    public Customer(int id, String name, String phone, String address) {
        super(id);
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean matches(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        String key = keyword.trim().toLowerCase();
        return contains(name, key) || contains(phone, key) || contains(address, key);
    }

    private static boolean contains(String text, String lowerKey) {
        return text != null && text.toLowerCase().contains(lowerKey);
    }

    /** Dipakai JComboBox (mis. dropdown customer di Kasir). */
    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}
