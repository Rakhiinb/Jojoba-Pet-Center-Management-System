package projek.jojobapetcentermanagementsystem.view;

/**
 * Diimplementasikan semua panel menu. MainFrame memanggil refresh()
 * setiap kali menu dipilih agar data (dropdown, tabel, angka) selalu terbaru.
 */
public interface Refreshable {

    void refresh();
}
