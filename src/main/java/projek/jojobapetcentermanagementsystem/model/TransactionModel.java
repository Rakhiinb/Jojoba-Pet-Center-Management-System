package projek.jojobapetcentermanagementsystem.model;

import java.time.LocalDateTime;

/** Baris ringkas transaksi untuk ditampilkan di tabel Riwayat. */
public class TransactionModel {

    private final int id;
    private final LocalDateTime date;
    private final String customerName;
    private final String petName;
    private final double total;

    /** Nama customer/hewan memakai default bila null: Umum / "-". */
    public TransactionModel(int id, LocalDateTime date, String customerName, String petName, double total) {
        this.id = id;
        this.date = date;
        this.customerName = (customerName == null || customerName.isBlank()) ? "Umum" : customerName;
        this.petName = (petName == null || petName.isBlank()) ? "-" : petName;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPetName() {
        return petName;
    }

    public double getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return date + " - " + customerName + " - " + total;
    }
}
