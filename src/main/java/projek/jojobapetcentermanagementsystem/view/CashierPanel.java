/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package projek.jojobapetcentermanagementsystem.view;

import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import projek.jojobapetcentermanagementsystem.entity.Billable;
import projek.jojobapetcentermanagementsystem.entity.Customer;
import projek.jojobapetcentermanagementsystem.entity.Pet;
import projek.jojobapetcentermanagementsystem.entity.Product;
import projek.jojobapetcentermanagementsystem.entity.Service;
import projek.jojobapetcentermanagementsystem.entity.Transaction;
import projek.jojobapetcentermanagementsystem.entity.TransactionItem;
import projek.jojobapetcentermanagementsystem.repository.CustomerRepository;
import projek.jojobapetcentermanagementsystem.repository.PetRepository;
import projek.jojobapetcentermanagementsystem.repository.ProductRepository;
import projek.jojobapetcentermanagementsystem.repository.ServiceRepository;
import projek.jojobapetcentermanagementsystem.repository.TransactionRepository;

/** Tab Kasir: merakit keranjang transaksi lalu menyimpannya secara atomik. */
public class CashierPanel extends javax.swing.JPanel implements Refreshable {

    private static final NumberFormat RUPIAH = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    private final CustomerRepository customerRepo = new CustomerRepository();
    private final PetRepository petRepo = new PetRepository();
    private final ProductRepository productRepo = new ProductRepository();
    private final ServiceRepository serviceRepo = new ServiceRepository();
    private final TransactionRepository transactionRepo = new TransactionRepository();

    private final DefaultTableModel cartModel = new DefaultTableModel(
            new Object[] {"Jenis", "Nama", "Harga", "Jumlah", "Subtotal"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private Transaction current = new Transaction();

    /**
     * Creates new form CashierPanel.
     * Menyusun form, keranjang, dan tombol aksi Kasir.
     */
    public CashierPanel() {
        initComponents();
        installPlaceholderRenderer(cmbCustomer, "- Pembeli Umum -");
        installPlaceholderRenderer(cmbPet, "- Tanpa Hewan -");
        spnQuantity.setModel(new SpinnerNumberModel(1, 1, 999, 1));
        tblCart.setModel(cartModel);
        lblTotal.setText("Total: " + RUPIAH.format(0));
        refresh();
    }

    private void cmbCustomerActionPerformed(java.awt.event.ActionEvent evt) {
        loadPets();
    }

    private void cmbItemTypeActionPerformed(java.awt.event.ActionEvent evt) {
        loadItems();
    }

    private void cmbItemActionPerformed(java.awt.event.ActionEvent evt) {
        updateStockHint();
    }

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {
        addToCart();
    }

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {
        removeFromCart();
    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
        saveTransaction();
    }

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {
        resetCart();
    }

    /** Memberi teks pengganti pada pilihan null di JComboBox. */
    private static void installPlaceholderRenderer(javax.swing.JComboBox<?> combo, String placeholder) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText(placeholder);
                }
                return this;
            }
        });
    }

    /** Memuat ulang dropdown; keranjang sengaja dipertahankan. */
    @Override
    public void refresh() {
        loadCustomers();
        loadPets();
        loadItems();
        updateStockHint();
    }

    /** Mengisi dropdown customer dan mempertahankan pilihan lama berdasarkan id. */
    private void loadCustomers() {
        int selectedId = cmbCustomer.getSelectedItem() instanceof Customer c ? c.getId() : -1;
        cmbCustomer.removeAllItems();
        cmbCustomer.addItem(null);
        for (Customer customer : customerRepo.findAll()) {
            cmbCustomer.addItem(customer);
            if (customer.getId() == selectedId) {
                cmbCustomer.setSelectedItem(customer);
            }
        }
    }

    /** Mengisi hewan milik customer terpilih; hewan bisa dikosongkan. */
    private void loadPets() {
        Customer customer = (Customer) cmbCustomer.getSelectedItem();
        int selectedId = cmbPet.getSelectedItem() instanceof Pet p ? p.getId() : -1;
        cmbPet.removeAllItems();
        cmbPet.setEnabled(customer != null);
        if (customer == null) {
            return;
        }
        cmbPet.addItem(null);
        for (Pet pet : petRepo.findByCustomer(customer.getId())) {
            cmbPet.addItem(pet);
            if (pet.getId() == selectedId) {
                cmbPet.setSelectedItem(pet);
            }
        }
    }

    /** Mengisi dropdown item sesuai jenis yang dipilih. */
    private void loadItems() {
        boolean produk = "Produk".equals(cmbItemType.getSelectedItem());
        int selectedId = cmbItem.getSelectedItem() instanceof Billable b ? b.getItemId() : -1;
        cmbItem.removeAllItems();
        if (produk) {
            for (Product product : productRepo.findAll()) {
                cmbItem.addItem(product);
                if (product.getItemId() == selectedId) {
                    cmbItem.setSelectedItem(product);
                }
            }
        } else {
            for (Service service : serviceRepo.findAll()) {
                cmbItem.addItem(service);
                if (service.getItemId() == selectedId) {
                    cmbItem.setSelectedItem(service);
                }
            }
        }
    }

    /** Menampilkan sisa stok untuk produk; layanan tidak punya stok. */
    private void updateStockHint() {
        if (cmbItem.getSelectedItem() instanceof Product product) {
            lblStock.setText("Stok tersedia: " + product.getStock());
        } else {
            lblStock.setText("Layanan tidak punya stok");
        }
    }

    /** Menambah item ke keranjang setelah memeriksa stok produk. */
    private void addToCart() {
        if (!(cmbItem.getSelectedItem() instanceof Billable item)) {
            JOptionPane.showMessageDialog(this, "Pilih item terlebih dahulu.",
                    "Kasir", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int quantity = (Integer) spnQuantity.getValue();
        if (item instanceof Product product && quantity > product.getStock()) {
            JOptionPane.showMessageDialog(this,
                    "Stok tidak cukup. Tersedia: " + product.getStock(),
                    "Kasir", JOptionPane.WARNING_MESSAGE);
            return;
        }
        current.addItem(item, quantity);
        renderCart();
    }

    /** Menghapus baris keranjang yang sedang dipilih. */
    private void removeFromCart() {
        int row = tblCart.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang akan dihapus.",
                    "Kasir", JOptionPane.WARNING_MESSAGE);
            return;
        }
        current.removeItem(row);
        renderCart();
    }

    /** Menyimpan transaksi; stok produk dikurangi di dalam satu transaksi DB. */
    private void saveTransaction() {
        if (current.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong.",
                    "Kasir", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Customer customer = (Customer) cmbCustomer.getSelectedItem();
        Pet pet = (Pet) cmbPet.getSelectedItem();
        current.setCustomerId(customer == null ? null : customer.getId());
        current.setPetId(pet == null ? null : pet.getId());
        String notes = txtNotes.getText().trim();
        current.setNotes(notes.isEmpty() ? null : notes);
        current.calculateTotal();

        if (!transactionRepo.save(current)) {
            JOptionPane.showMessageDialog(this, "Transaksi gagal disimpan. Periksa stok dan koneksi database.",
                    "Kasir", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Transaksi tersimpan. Total " + RUPIAH.format(current.getTotal()),
                "Kasir", JOptionPane.INFORMATION_MESSAGE);
        resetCart();
        refresh();
    }

    /** Mengosongkan keranjang dan form setelah transaksi berhasil atau tombol Reset. */
    private void resetCart() {
        current = new Transaction();
        cartModel.setRowCount(0);
        lblTotal.setText("Total: " + RUPIAH.format(0));
        txtNotes.setText("");
        spnQuantity.setValue(1);
    }

    /** Menampilkan isi keranjang dan total terbaru. */
    private void renderCart() {
        cartModel.setRowCount(0);
        for (TransactionItem item : current.getItems()) {
            cartModel.addRow(new Object[] {
                    item.getItemType(),
                    item.getDescription(),
                    RUPIAH.format(item.getUnitPrice()),
                    item.getQuantity(),
                    RUPIAH.format(item.getSubtotal())});
        }
        lblTotal.setText("Total: " + RUPIAH.format(current.calculateTotal()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelForm = new javax.swing.JPanel();
        lblCustomer = new javax.swing.JLabel();
        cmbCustomer = new javax.swing.JComboBox<>();
        lblPet = new javax.swing.JLabel();
        cmbPet = new javax.swing.JComboBox<>();
        lblItemType = new javax.swing.JLabel();
        cmbItemType = new javax.swing.JComboBox<>();
        cmbItemType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {"Produk", "Layanan"}));
        lblItem = new javax.swing.JLabel();
        cmbItem = new javax.swing.JComboBox<>();
        lblQuantity = new javax.swing.JLabel();
        spnQuantity = new javax.swing.JSpinner();
        lblStock = new javax.swing.JLabel();
        lblNotes = new javax.swing.JLabel();
        scrollPaneNotes = new javax.swing.JScrollPane();
        txtNotes = new javax.swing.JTextArea();
        scrollPaneCart = new javax.swing.JScrollPane();
        tblCart = new javax.swing.JTable();
        panelActions = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        lblTotal = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(800, 600));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));

        panelForm.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Transaksi"));

        lblCustomer.setText("Customer");

        cmbCustomer.addActionListener(this::cmbCustomerActionPerformed);

        lblPet.setText("Hewan");

        lblItemType.setText("Jenis Item");

        cmbItemType.addActionListener(this::cmbItemTypeActionPerformed);

        lblItem.setText("Item");

        cmbItem.addActionListener(this::cmbItemActionPerformed);

        lblQuantity.setText("Jumlah");

        lblStock.setText("Stok tersedia: -");

        lblNotes.setText("Catatan");

        txtNotes.setColumns(20);
        txtNotes.setRows(2);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        scrollPaneNotes.setViewportView(txtNotes);

        javax.swing.GroupLayout panelFormLayout = new javax.swing.GroupLayout(panelForm);
        panelForm.setLayout(panelFormLayout);
        panelFormLayout.setHorizontalGroup(
            panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCustomer)
                    .addComponent(lblPet)
                    .addComponent(lblItemType)
                    .addComponent(lblItem)
                    .addComponent(lblQuantity)
                    .addComponent(lblNotes))
                .addGap(18, 18, 18)
                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbCustomer, 0, 180, Short.MAX_VALUE)
                    .addComponent(cmbPet, 0, 180, Short.MAX_VALUE)
                    .addComponent(cmbItemType, 0, 180, Short.MAX_VALUE)
                    .addComponent(cmbItem, 0, 180, Short.MAX_VALUE)
                    .addGroup(panelFormLayout.createSequentialGroup()
                        .addComponent(spnQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblStock))
                    .addComponent(scrollPaneNotes, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelFormLayout.setVerticalGroup(
            panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCustomer)
                    .addComponent(cmbCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPet)
                    .addComponent(cmbPet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblItemType)
                    .addComponent(cmbItemType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblItem)
                    .addComponent(cmbItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblQuantity)
                    .addComponent(spnQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStock))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNotes)
                    .addComponent(scrollPaneNotes, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE))
                .addContainerGap())
        );

        scrollPaneCart.setViewportView(tblCart);

        btnAdd.setText("Tambah ke Keranjang");
        btnAdd.addActionListener(this::btnAddActionPerformed);

        btnRemove.setText("Hapus Item");
        btnRemove.addActionListener(this::btnRemoveActionPerformed);

        btnSave.setText("Simpan Transaksi");
        btnSave.addActionListener(this::btnSaveActionPerformed);

        btnReset.setText("Reset");
        btnReset.addActionListener(this::btnResetActionPerformed);

        javax.swing.GroupLayout panelActionsLayout = new javax.swing.GroupLayout(panelActions);
        panelActions.setLayout(panelActionsLayout);
        panelActionsLayout.setHorizontalGroup(
            panelActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAdd)
                .addGap(6, 6, 6)
                .addComponent(btnRemove)
                .addGap(6, 6, 6)
                .addComponent(btnSave)
                .addGap(6, 6, 6)
                .addComponent(btnReset)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTotal)
                .addContainerGap())
        );
        panelActionsLayout.setVerticalGroup(
            panelActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnRemove)
                    .addComponent(btnSave)
                    .addComponent(btnReset)
                    .addComponent(lblTotal))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scrollPaneCart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addComponent(panelForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(scrollPaneCart, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(panelActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cmbCustomer;
    private javax.swing.JComboBox cmbItem;
    private javax.swing.JComboBox<String> cmbItemType;
    private javax.swing.JComboBox cmbPet;
    private javax.swing.JLabel lblCustomer;
    private javax.swing.JLabel lblItem;
    private javax.swing.JLabel lblItemType;
    private javax.swing.JLabel lblNotes;
    private javax.swing.JLabel lblPet;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblStock;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel panelActions;
    private javax.swing.JPanel panelForm;
    private javax.swing.JScrollPane scrollPaneCart;
    private javax.swing.JScrollPane scrollPaneNotes;
    private javax.swing.JSpinner spnQuantity;
    private javax.swing.JTable tblCart;
    private javax.swing.JTextArea txtNotes;
    // End of variables declaration//GEN-END:variables
}
