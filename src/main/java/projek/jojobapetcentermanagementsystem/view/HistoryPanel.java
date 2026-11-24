package projek.jojobapetcentermanagementsystem.view;

import projek.jojobapetcentermanagementsystem.entity.TransactionItem;
import projek.jojobapetcentermanagementsystem.model.TransactionModel;
import projek.jojobapetcentermanagementsystem.repository.TransactionRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/** Panel Riwayat: menampilkan transaksi per rentang tanggal dan rincian itemnya. */
public class HistoryPanel extends JPanel implements Refreshable {

    private static final String[] TRANSACTION_COLUMNS = {"ID", "Tanggal", "Customer", "Hewan", "Total"};
    private static final String[] ITEM_COLUMNS = {"ID", "Jenis", "Deskripsi", "Qty", "Harga", "Subtotal"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    private final TransactionRepository repository = new TransactionRepository();
    private final JTextField fromField = new JTextField(12);
    private final JTextField toField = new JTextField(12);
    private final JButton cariButton = new JButton("Cari");
    private final JButton resetButton = new JButton("Reset");
    private final JLabel totalLabel = new JLabel();

    private final DefaultTableModel transactionModel = new DefaultTableModel(TRANSACTION_COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel itemModel = new DefaultTableModel(ITEM_COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable transactionTable = new JTable(transactionModel);
    private final JTable itemTable = new JTable(itemModel);

    private List<TransactionModel> currentTransactions = List.of();

    /** Constructor: menyusun filter, tabel transaksi, tabel rincian, dan label total. */
    public HistoryPanel() {
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        fromField.setToolTipText("yyyy-MM-dd, kosong = tanpa batas");
        toField.setToolTipText("yyyy-MM-dd, kosong = tanpa batas");
        cariButton.addActionListener(e -> refresh());
        resetButton.addActionListener(e -> {
            fromField.setText("");
            toField.setText("");
            refresh();
        });

        JPanel filter = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        filter.add(new JLabel("Dari"), gbc);
        gbc.gridx = 1;
        filter.add(fromField, gbc);
        gbc.gridx = 2;
        filter.add(new JLabel("Sampai"), gbc);
        gbc.gridx = 3;
        filter.add(toField, gbc);
        gbc.gridx = 4;
        filter.add(cariButton, gbc);
        gbc.gridx = 5;
        filter.add(resetButton, gbc);
        filter.setBorder(BorderFactory.createTitledBorder("Filter periode"));
        add(filter, BorderLayout.NORTH);

        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setRowHeight(24);
        transactionTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int row = transactionTable.getSelectedRow();
            if (row < 0) {
                itemModel.setRowCount(0);
                return;
            }
            loadItems((Integer) transactionModel.getValueAt(row, 0));
        });
        itemTable.setRowHeight(24);

        JScrollPane transactionScroll = new JScrollPane(transactionTable);
        transactionScroll.setBorder(BorderFactory.createTitledBorder("Transaksi"));
        JScrollPane itemScroll = new JScrollPane(itemTable);
        itemScroll.setBorder(BorderFactory.createTitledBorder("Rincian item"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, transactionScroll, itemScroll);
        split.setResizeWeight(0.55);
        add(split, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.add(totalLabel);
        add(footer, BorderLayout.SOUTH);

        refresh();
    }

    /** Memuat ulang tabel sesuai rentang tanggal di filter; MainFrame memanggil ini saat menu dipilih. */
    @Override
    public void refresh() {
        LocalDate from;
        LocalDate to;
        try {
            from = parseDate(fromField.getText());
            to = parseDate(toField.getText());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Format tanggal harus yyyy-MM-dd.",
                    "Tanggal tidak valid",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (from != null && to != null && from.isAfter(to)) {
            JOptionPane.showMessageDialog(this,
                    "Tanggal 'Dari' tidak boleh setelah tanggal 'Sampai'.",
                    "Rentang tidak valid",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentTransactions = repository.findByDateRange(from, to);
        transactionModel.setRowCount(0);
        for (TransactionModel transaction : currentTransactions) {
            transactionModel.addRow(new Object[]{
                    transaction.getId(),
                    DATE_FORMAT.format(transaction.getDate()),
                    transaction.getCustomerName(),
                    transaction.getPetName(),
                    CURRENCY.format(transaction.getTotal())
            });
        }
        itemModel.setRowCount(0);
        updateTotalLabel();
    }

    /** Tanggal ISO yyyy-MM-dd; teks kosong menjadi null sebagai batas tak terisi. */
    private static LocalDate parseDate(String text) {
        String value = (text == null) ? "" : text.trim();
        if (value.isEmpty()) {
            return null;
        }
        return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /** Memuat item dari transaksi yang sedang dipilih ke tabel rincian. */
    private void loadItems(int transactionId) {
        itemModel.setRowCount(0);
        for (TransactionItem item : repository.findItems(transactionId)) {
            String type = "PRODUCT".equalsIgnoreCase(item.getItemType()) ? "Produk" : "Layanan";
            itemModel.addRow(new Object[]{
                    item.getId(),
                    type,
                    item.getDescription(),
                    item.getQuantity(),
                    CURRENCY.format(item.getUnitPrice()),
                    CURRENCY.format(item.getSubtotal())
            });
        }
    }

    /** Menampilkan total pendapatan untuk semua transaksi yang sedang tampil. */
    private void updateTotalLabel() {
        double total = 0;
        for (TransactionModel transaction : currentTransactions) {
            total += transaction.getTotal();
        }
        totalLabel.setText("Total pendapatan periode: " + CURRENCY.format(total));
    }
}
