/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package projek.jojobapetcentermanagementsystem.view;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import projek.jojobapetcentermanagementsystem.entity.Product;
import projek.jojobapetcentermanagementsystem.repository.ProductRepository;


/**
 *
 * @author Daniel
 */
public class ProductPanel extends javax.swing.JPanel implements Refreshable {

    private final ProductRepository productRepo = new ProductRepository();
    private int selectedId = -1;
    /**
     * Creates new form ProductPanel
     */
    public ProductPanel() {
        initComponents();
        initButtonListeners();
        refresh();
    }

    private void initButtonListeners() {
        btnAddProduct.addActionListener(e -> handleAdd());
        btnUpdateProduct.addActionListener(e -> handleUpdate());
        btnDeleteProduct.addActionListener(e -> handleDelete());
        btnClearProduct.addActionListener(e -> clearForm());
    }

    private void populateTable(List<Product> list) {
        DefaultTableModel model = (DefaultTableModel) tblProducts.getModel();
        model.setRowCount(0);
        for (Product p : list) {
            model.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getCategory(),
                p.getPrice(),
                p.getStock()
            });
        }
    }

    @Override
    public void refresh() {
        populateTable(productRepo.findAll());
    }

    public void search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            refresh();
        } else {
            populateTable(productRepo.search(keyword.trim()));
        }
    }

    private void clearForm() {
        selectedId = -1;
        txtProductName.setText("");
        cmbProductCategory.setSelectedIndex(0);
        txtProductPrice.setText("");
        txtProductStock.setText("");
        tblProducts.clearSelection();
    }

    private void handleAdd() {
        try {
            String name = txtProductName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama produk wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String category = cmbProductCategory.getSelectedItem().toString();
            double price = Double.parseDouble(txtProductPrice.getText().trim());
            int stock = Integer.parseInt(txtProductStock.getText().trim());

            if (price <= 0 || stock < 0) {
                JOptionPane.showMessageDialog(this, "Harga harus > 0 dan stok tidak boleh negatif!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (productRepo.save(new Product(name, category, price, stock))) {
                JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan.");
                clearForm();
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan produk.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka yang valid!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk di tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String name = txtProductName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama produk wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String category = cmbProductCategory.getSelectedItem().toString();
            double price = Double.parseDouble(txtProductPrice.getText().trim());
            int stock = Integer.parseInt(txtProductStock.getText().trim());

            if (price <= 0 || stock < 0) {
                JOptionPane.showMessageDialog(this, "Harga harus > 0 dan stok tidak boleh negatif!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (productRepo.update(new Product(selectedId, name, category, price, stock))) {
                JOptionPane.showMessageDialog(this, "Produk berhasil diperbarui.");
                clearForm();
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui produk.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka yang valid!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus produk ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (productRepo.delete(selectedId)) {
                JOptionPane.showMessageDialog(this, "Produk berhasil dihapus.");
                clearForm();
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus! Produk masih digunakan dalam riwayat transaksi.", "Ditolak", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFormProduct = new javax.swing.JPanel();
        txtProductPrice = new javax.swing.JTextField();
        lblProductName = new javax.swing.JLabel();
        lblProductCategory = new javax.swing.JLabel();
        cmbProductCategory = new javax.swing.JComboBox<>();
        lblProductPrice = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        lblProductStock = new javax.swing.JLabel();
        txtProductStock = new javax.swing.JTextField();
        lblProductAction = new javax.swing.JLabel();
        btnAddProduct = new javax.swing.JButton();
        btnUpdateProduct = new javax.swing.JButton();
        btnDeleteProduct = new javax.swing.JButton();
        btnClearProduct = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtSearchProduct = new javax.swing.JTextField();
        scrollPaneProducts = new javax.swing.JScrollPane();
        tblProducts = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(800, 600));

        panelFormProduct.setBorder(javax.swing.BorderFactory.createTitledBorder("Form Data Produk"));

        txtProductPrice.addActionListener(this::txtProductPriceActionPerformed);

        lblProductName.setText("Nama Produk : ");

        lblProductCategory.setText("Kategori :");

        cmbProductCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Makanan", "Aksesoris", "Obat" }));
        cmbProductCategory.addActionListener(this::cmbProductCategoryActionPerformed);

        lblProductPrice.setText("Harga (Rp) :");

        txtProductName.addActionListener(this::txtProductNameActionPerformed);

        lblProductStock.setText("Stok :");

        txtProductStock.addActionListener(this::txtProductStockActionPerformed);

        lblProductAction.setText("Aksi :");

        btnAddProduct.setText("Tambah");

        btnUpdateProduct.setText("Ubah");

        btnDeleteProduct.setText("Hapus");

        btnClearProduct.setText("Reset");

        javax.swing.GroupLayout panelFormProductLayout = new javax.swing.GroupLayout(panelFormProduct);
        panelFormProduct.setLayout(panelFormProductLayout);
        panelFormProductLayout.setHorizontalGroup(
            panelFormProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormProductLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFormProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblProductName)
                    .addComponent(lblProductCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductStock, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductAction, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addGroup(panelFormProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFormProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtProductPrice, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                        .addComponent(txtProductStock)
                        .addComponent(cmbProductCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelFormProductLayout.createSequentialGroup()
                        .addGroup(panelFormProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(panelFormProductLayout.createSequentialGroup()
                                .addComponent(btnAddProduct)
                                .addGap(34, 34, 34)
                                .addComponent(btnUpdateProduct)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnDeleteProduct))
                            .addComponent(txtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38)
                        .addComponent(btnClearProduct)))
                .addContainerGap(212, Short.MAX_VALUE))
        );
        panelFormProductLayout.setVerticalGroup(
            panelFormProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormProductLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelFormProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductName)
                    .addComponent(txtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelFormProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductCategory)
                    .addComponent(cmbProductCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFormProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProductPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductPrice))
                .addGap(18, 18, 18)
                .addGroup(panelFormProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductStock)
                    .addComponent(txtProductStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(panelFormProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductAction)
                    .addComponent(btnAddProduct)
                    .addComponent(btnUpdateProduct)
                    .addComponent(btnDeleteProduct)
                    .addComponent(btnClearProduct))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jLabel6.setText("Cari Produk :");

        txtSearchProduct.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchProductKeyReleased(evt);
            }
        });

        tblProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nama Produk", "Kategori", "Harga", "Stok"
            }
        ));
        tblProducts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductsMouseClicked(evt);
            }
        });
        scrollPaneProducts.setViewportView(tblProducts);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFormProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollPaneProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 745, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(txtSearchProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(panelFormProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtSearchProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(72, 72, 72)
                .addComponent(scrollPaneProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(176, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtProductPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProductPriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProductPriceActionPerformed

    private void cmbProductCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProductCategoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbProductCategoryActionPerformed

    private void txtProductNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProductNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProductNameActionPerformed

    private void txtProductStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProductStockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProductStockActionPerformed

    private void txtSearchProductKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchProductKeyReleased
        // TODO add your handling code here:
        search(txtSearchProduct.getText());
    }//GEN-LAST:event_txtSearchProductKeyReleased

    private void tblProductsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductsMouseClicked
        // TODO add your handling code here:
        int row = tblProducts.getSelectedRow();
        if (row != -1) {
            selectedId = Integer.parseInt(tblProducts.getValueAt(row, 0).toString());
            txtProductName.setText(tblProducts.getValueAt(row, 1).toString());
            cmbProductCategory.setSelectedItem(tblProducts.getValueAt(row, 2).toString());
            txtProductPrice.setText(tblProducts.getValueAt(row, 3).toString());
            txtProductStock.setText(tblProducts.getValueAt(row, 4).toString());
        }
    }//GEN-LAST:event_tblProductsMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddProduct;
    private javax.swing.JButton btnClearProduct;
    private javax.swing.JButton btnDeleteProduct;
    private javax.swing.JButton btnUpdateProduct;
    private javax.swing.JComboBox<String> cmbProductCategory;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel lblProductAction;
    private javax.swing.JLabel lblProductCategory;
    private javax.swing.JLabel lblProductName;
    private javax.swing.JLabel lblProductPrice;
    private javax.swing.JLabel lblProductStock;
    private javax.swing.JPanel panelFormProduct;
    private javax.swing.JScrollPane scrollPaneProducts;
    private javax.swing.JTable tblProducts;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtProductPrice;
    private javax.swing.JTextField txtProductStock;
    private javax.swing.JTextField txtSearchProduct;
    // End of variables declaration//GEN-END:variables
}
