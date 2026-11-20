package projek.jojobapetcentermanagementsystem.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
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
public class CashierPanel extends JPanel implements Refreshable {

    private static final NumberFormat RUPIAH = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    private final CustomerRepository customerRepo = new CustomerRepository();
    private final PetRepository petRepo = new PetRepository();
    private final ProductRepository productRepo = new ProductRepository();
    private final ServiceRepository serviceRepo = new ServiceRepository();
    private final TransactionRepository transactionRepo = new TransactionRepository();

    private final JComboBox<Customer> cmbCustomer = new JComboBox<>();
    private final JComboBox<Pet> cmbPet = new JComboBox<>();
    private final JComboBox<String> cmbItemType = new JComboBox<>(new String[] {"Produk", "Layanan"});
    private final JComboBox<Billable> cmbItem = new JComboBox<>();
    private final JSpinner spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JLabel lblStock = new JLabel("Stok tersedia: -");
    private final JTextArea txtNotes = new JTextArea(2, 24);

    private final DefaultTableModel cartModel = new DefaultTableModel(
            new Object[] {"Jenis", "Nama", "Harga", "Jumlah", "Subtotal"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tblCart = new JTable(cartModel);
    private final JLabel lblTotal = new JLabel("Total: " + RUPIAH.format(0));

    private Transaction current = new Transaction();

    /** Menyusun form, keranjang, dan tombol aksi Kasir. */
    public CashierPanel() {
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buildForm(), BorderLayout.NORTH);
        add(new JScrollPane(tblCart), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);

        installPlaceholderRenderer(cmbCustomer, "- Pembeli Umum -");
        installPlaceholderRenderer(cmbPet, "- Tanpa Hewan -");

        cmbCustomer.addActionListener(e -> loadPets());
        cmbItemType.addActionListener(e -> loadItems());
        cmbItem.addActionListener(e -> updateStockHint());

        refresh();
    }

    /** Form customer, hewan, jenis item, jumlah, dan catatan. */
    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Data Transaksi"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;

        addField(form, c, 0, 0, "Customer", cmbCustomer);
        addField(form, c, 1, 0, "Hewan", cmbPet);
        addField(form, c, 0, 1, "Jenis Item", cmbItemType);
        addField(form, c, 1, 1, "Item", cmbItem);
        addField(form, c, 0, 2, "Jumlah", spnQuantity);

        c.gridx = 1;
        c.gridy = 2;
        form.add(lblStock, c);

        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        form.add(new JLabel("Catatan"), c);
        c.gridx = 1;
        form.add(new JScrollPane(txtNotes), c);
        return form;
    }

    /** Tombol tambah, hapus, simpan, dan reset keranjang. */
    private JPanel buildActions() {
        JButton btnAdd = new JButton("Tambah ke Keranjang");
        JButton btnRemove = new JButton("Hapus Item");
        JButton btnSave = new JButton("Simpan Transaksi");
        JButton btnReset = new JButton("Reset");

        btnAdd.addActionListener(e -> addToCart());
        btnRemove.addActionListener(e -> removeFromCart());
        btnSave.addActionListener(e -> saveTransaction());
        btnReset.addActionListener(e -> resetCart());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        actions.add(btnAdd);
        actions.add(btnRemove);
        actions.add(btnSave);
        actions.add(btnReset);
        actions.add(lblTotal);
        return actions;
    }

    /** Menaruh satu label dan satu komponen ke grid form. */
    private static void addField(JPanel panel, GridBagConstraints c, int x, int y, String label, JComponent field) {
        c.gridx = x;
        c.gridy = y;
        panel.add(new JLabel(label), c);
        c.gridx = x + 1;
        panel.add(field, c);
    }

    /** Memberi teks pengganti pada pilihan null di JComboBox. */
    private static void installPlaceholderRenderer(JComboBox<?> combo, String placeholder) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
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
}
