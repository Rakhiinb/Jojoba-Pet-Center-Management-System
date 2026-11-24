package projek.jojobapetcentermanagementsystem.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Satu transaksi kasir: header transaksi plus daftar item yang dibeli. */
public class Transaction extends BaseEntity {

    private Integer customerId;
    private Integer petId;
    private LocalDateTime date;
    private String notes;
    private double total;
    private List<TransactionItem> items;

    public Transaction() {
        this.date = LocalDateTime.now();
        this.items = new ArrayList<>();
    }

    /** Constructor tanpa id: transaksi baru dengan tanggal saat ini. */
    public Transaction(Integer customerId, Integer petId, String notes) {
        this();
        this.customerId = customerId;
        this.petId = petId;
        this.notes = notes;
    }

    /** Constructor dengan id: untuk data yang dibaca dari database. */
    public Transaction(int id, Integer customerId, Integer petId, LocalDateTime date, String notes, double total) {
        super(id);
        this.customerId = customerId;
        this.petId = petId;
        this.date = date;
        this.notes = notes;
        this.total = total;
        this.items = new ArrayList<>();
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getPetId() {
        return petId;
    }

    public void setPetId(Integer petId) {
        this.petId = petId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<TransactionItem> getItems() {
        return items;
    }

    public void setItems(List<TransactionItem> items) {
        this.items = (items != null) ? items : new ArrayList<>();
    }

    /** Menambah item ke keranjang lalu menghitung ulang total. */
    public void addItem(TransactionItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        calculateTotal();
    }

    /** Menambah Product/Service langsung ke keranjang. */
    public void addItem(Billable item, int quantity) {
        addItem(new TransactionItem(item, quantity));
    }

    /** Menghapus item berdasarkan index lalu menghitung ulang total. */
    public void removeItem(int index) {
        if (items != null && index >= 0 && index < items.size()) {
            items.remove(index);
            calculateTotal();
        }
    }

    /** Menjumlahkan subtotal seluruh item. */
    public double calculateTotal() {
        double sum = 0;
        if (items != null) {
            for (TransactionItem item : items) {
                sum += item.getSubtotal();
            }
        }
        this.total = sum;
        return sum;
    }
}
