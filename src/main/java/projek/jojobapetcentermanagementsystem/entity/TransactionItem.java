package projek.jojobapetcentermanagementsystem.entity;

/** Satu baris item pada transaksi: produk atau layanan yang dibeli. */
public class TransactionItem extends BaseEntity {

    private String itemType;
    private int itemId;
    private String description;
    private double unitPrice;
    private int quantity;

    public TransactionItem() {
    }

    /** Constructor tanpa id: item baru yang belum disimpan. */
    public TransactionItem(String itemType, int itemId, String description, double unitPrice, int quantity) {
        this.itemType = itemType;
        this.itemId = itemId;
        this.description = description;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /** Constructor dari Product/Service: menyalin jenis, id, nama, dan harga. */
    public TransactionItem(Billable item, int quantity) {
        this(item.getItemType(), item.getItemId(), item.getItemName(), item.getPrice(), quantity);
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /** Harga satuan dikali jumlah item. */
    public double getSubtotal() {
        return unitPrice * quantity;
    }

    @Override
    public String toString() {
        return description + " x" + quantity;
    }
}
