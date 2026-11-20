package projek.jojobapetcentermanagementsystem.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/** Panel Transaksi: menggabungkan tab Kasir dan Riwayat. */
public class TransactionPanel extends JPanel implements Refreshable {

    private final CashierPanel cashierPanel = new CashierPanel();
    private final HistoryPanel historyPanel = new HistoryPanel();

    /** Membuat panel transaksi dengan dua tab. */
    public TransactionPanel() {
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Kasir", cashierPanel);
        tabs.addTab("Riwayat", historyPanel);
        add(tabs, BorderLayout.CENTER);
    }

    /** Menyegarkan kedua tab saat menu dipilih. */
    @Override
    public void refresh() {
        cashierPanel.refresh();
        historyPanel.refresh();
    }
}
