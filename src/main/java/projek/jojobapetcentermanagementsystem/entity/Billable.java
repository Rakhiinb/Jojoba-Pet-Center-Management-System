package projek.jojobapetcentermanagementsystem.entity;

/**
 * Sesuatu yang bisa ditagihkan di Kasir (produk atau layanan).
 * Keranjang memperlakukan keduanya sama lewat interface ini (polymorphism).
 */
public interface Billable {

    int getItemId();

    /** "PRODUCT" atau "SERVICE". */
    String getItemType();

    String getItemName();

    double getPrice();
}
