/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package projek.jojobapetcentermanagementsystem.view;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import projek.jojobapetcentermanagementsystem.entity.TransactionItem;
import projek.jojobapetcentermanagementsystem.model.TransactionModel;
import projek.jojobapetcentermanagementsystem.repository.TransactionRepository;

/** Panel Riwayat: menampilkan transaksi per rentang tanggal dan rincian itemnya. */
public class HistoryPanel extends javax.swing.JPanel implements Refreshable {

    private static final String[] TRANSACTION_COLUMNS = {"ID", "Tanggal", "Customer", "Hewan", "Total"};
    private static final String[] ITEM_COLUMNS = {"ID", "Jenis", "Deskripsi", "Qty", "Harga", "Subtotal"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    private final TransactionRepository repository = new TransactionRepository();

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

    private List<TransactionModel> currentTransactions = List.of();

    /**
     * Creates new form HistoryPanel.
     * Menyusun filter, tabel transaksi, tabel rincian, dan label total.
     */
    public HistoryPanel() {
        initComponents();
        transactionTable.setModel(transactionModel);
        itemTable.setModel(itemModel);
        refresh();
    }

    private void cariButtonActionPerformed(java.awt.event.ActionEvent evt) {
        refresh();
    }

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {
        fromField.setText("");
        toField.setText("");
        refresh();
    }

    private void transactionTableValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (evt.getValueIsAdjusting()) {
            return;
        }
        int row = transactionTable.getSelectedRow();
        if (row < 0) {
            itemModel.setRowCount(0);
            return;
        }
        loadItems((Integer) transactionModel.getValueAt(row, 0));
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
            transactionModel.addRow(new Object[] {
                    transaction.getId(),
                    DATE_FORMAT.format(transaction.getDate()),
                    transaction.getCustomerName(),
                    transaction.getPetName(),
                    CURRENCY.format(transaction.getTotal())});
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
            itemModel.addRow(new Object[] {
                    item.getId(),
                    type,
                    item.getDescription(),
                    item.getQuantity(),
                    CURRENCY.format(item.getUnitPrice()),
                    CURRENCY.format(item.getSubtotal())});
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFilter = new javax.swing.JPanel();
        lblFrom = new javax.swing.JLabel();
        fromField = new javax.swing.JTextField();
        lblTo = new javax.swing.JLabel();
        toField = new javax.swing.JTextField();
        cariButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        splitPane = new javax.swing.JSplitPane();
        scrollPaneTransaction = new javax.swing.JScrollPane();
        transactionTable = new javax.swing.JTable();
        scrollPaneItem = new javax.swing.JScrollPane();
        itemTable = new javax.swing.JTable();
        panelFooter = new javax.swing.JPanel();
        totalLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(800, 600));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));

        panelFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter periode"));

        lblFrom.setText("Dari");

        fromField.setToolTipText("yyyy-MM-dd, kosong = tanpa batas");

        lblTo.setText("Sampai");

        toField.setToolTipText("yyyy-MM-dd, kosong = tanpa batas");

        cariButton.setText("Cari");
        cariButton.addActionListener(this::cariButtonActionPerformed);

        resetButton.setText("Reset");
        resetButton.addActionListener(this::resetButtonActionPerformed);

        javax.swing.GroupLayout panelFilterLayout = new javax.swing.GroupLayout(panelFilter);
        panelFilter.setLayout(panelFilterLayout);
        panelFilterLayout.setHorizontalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFrom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fromField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cariButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resetButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelFilterLayout.setVerticalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFrom)
                    .addComponent(fromField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTo)
                    .addComponent(toField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cariButton)
                    .addComponent(resetButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.55);

        scrollPaneTransaction.setBorder(javax.swing.BorderFactory.createTitledBorder("Transaksi"));

        transactionTable.setRowHeight(24);
        transactionTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        transactionTable.getSelectionModel().addListSelectionListener(this::transactionTableValueChanged);
        scrollPaneTransaction.setViewportView(transactionTable);

        splitPane.setLeftComponent(scrollPaneTransaction);

        scrollPaneItem.setBorder(javax.swing.BorderFactory.createTitledBorder("Rincian item"));

        itemTable.setRowHeight(24);
        scrollPaneItem.setViewportView(itemTable);

        splitPane.setRightComponent(scrollPaneItem);

        panelFooter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        panelFooter.add(totalLabel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelFooter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(panelFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cariButton;
    private javax.swing.JTextField fromField;
    private javax.swing.JTable itemTable;
    private javax.swing.JLabel lblFrom;
    private javax.swing.JLabel lblTo;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JPanel panelFooter;
    private javax.swing.JButton resetButton;
    private javax.swing.JScrollPane scrollPaneItem;
    private javax.swing.JScrollPane scrollPaneTransaction;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTextField toField;
    private javax.swing.JLabel totalLabel;
    private javax.swing.JTable transactionTable;
    // End of variables declaration//GEN-END:variables
}
