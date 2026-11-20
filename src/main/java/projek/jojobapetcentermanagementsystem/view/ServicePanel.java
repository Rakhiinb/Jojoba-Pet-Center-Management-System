/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package projek.jojobapetcentermanagementsystem.view;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import projek.jojobapetcentermanagementsystem.entity.Service;
import projek.jojobapetcentermanagementsystem.repository.ServiceRepository;

/**
 *
 * @author Daniel
 */
public class ServicePanel extends javax.swing.JPanel implements Refreshable {
    
    private final ServiceRepository serviceRepo = new ServiceRepository();
    private int selectedId = -1;
    
    /**
     * Creates new form ServicePanel
     */
    public ServicePanel() {
        initComponents();
        refresh();
    }
    
    private void handleAdd() {
        try {
            String name = txtServiceName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama layanan wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double price = Double.parseDouble(txtServicePrice.getText().trim());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Harga harus lebih dari 0!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String unit = cmbServiceUnit.getSelectedItem().toString();

            Service s = new Service(name, price, unit);
            if (serviceRepo.save(s)) {
                JOptionPane.showMessageDialog(this, "Layanan berhasil ditambahkan.");
                clearForm();
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan layanan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka yang valid!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih layanan di tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String name = txtServiceName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama layanan wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double price = Double.parseDouble(txtServicePrice.getText().trim());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Harga harus lebih dari 0!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String unit = cmbServiceUnit.getSelectedItem().toString();

            Service s = new Service(selectedId, name, price, unit);
            if (serviceRepo.update(s)) {
                JOptionPane.showMessageDialog(this, "Layanan berhasil diperbarui.");
                clearForm();
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui layanan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka yang valid!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void handleDelete() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih layanan yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus layanan ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (serviceRepo.delete(selectedId)) {
                JOptionPane.showMessageDialog(this, "Layanan berhasil dihapus.");
                clearForm();
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus! Layanan masih digunakan dalam riwayat transaksi.", "Ditolak", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedId = -1;
        txtServiceName.setText("");
        txtServicePrice.setText("");
        cmbServiceUnit.setSelectedIndex(0);
        tblServices.clearSelection();
    }

    @Override
    public void refresh() {
        DefaultTableModel model = (DefaultTableModel) tblServices.getModel();
        model.setRowCount(0);
        List<Service> list = serviceRepo.findAll();
        for (Service s : list) {
            model.addRow(new Object[]{s.getId(), s.getName(), s.getPrice(), s.getUnit()});
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

        panelFormService = new javax.swing.JPanel();
        lblServiceName = new javax.swing.JLabel();
        lblServicePrice = new javax.swing.JLabel();
        lblServiceUnit = new javax.swing.JLabel();
        lblServiceAction = new javax.swing.JLabel();
        txtServiceName = new javax.swing.JTextField();
        txtServicePrice = new javax.swing.JTextField();
        btnAddService = new javax.swing.JButton();
        btnUpdateService = new javax.swing.JButton();
        btnDeleteService = new javax.swing.JButton();
        btnClearService = new javax.swing.JButton();
        cmbServiceUnit = new javax.swing.JComboBox<>();
        scrollPaneServices = new javax.swing.JScrollPane();
        tblServices = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(800, 600));

        panelFormService.setBorder(javax.swing.BorderFactory.createTitledBorder("Form Data Layanan"));

        lblServiceName.setText("Nama Layanan:");

        lblServicePrice.setText("Harga (Rp):");

        lblServiceUnit.setText("Satuan:");

        lblServiceAction.setText("Aksi:");

        txtServiceName.addActionListener(this::txtServiceNameActionPerformed);

        btnAddService.setText("Tambah");
        btnAddService.addActionListener(this::btnAddServiceActionPerformed);

        btnUpdateService.setText("Ubah");
        btnUpdateService.addActionListener(this::btnUpdateServiceActionPerformed);

        btnDeleteService.setText("Hapus");
        btnDeleteService.addActionListener(this::btnDeleteServiceActionPerformed);

        btnClearService.setText("Reset");
        btnClearService.addActionListener(this::btnClearServiceActionPerformed);

        cmbServiceUnit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbServiceUnit.setName("Kali, Hari"); // NOI18N

        javax.swing.GroupLayout panelFormServiceLayout = new javax.swing.GroupLayout(panelFormService);
        panelFormService.setLayout(panelFormServiceLayout);
        panelFormServiceLayout.setHorizontalGroup(
            panelFormServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFormServiceLayout.createSequentialGroup()
                .addGroup(panelFormServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblServicePrice)
                    .addComponent(lblServiceUnit)
                    .addComponent(lblServiceAction)
                    .addComponent(lblServiceName))
                .addGap(18, 18, 18)
                .addGroup(panelFormServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFormServiceLayout.createSequentialGroup()
                        .addGroup(panelFormServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cmbServiceUnit, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelFormServiceLayout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(btnAddService)
                                .addGap(60, 60, 60)
                                .addComponent(btnUpdateService)
                                .addGap(52, 52, 52)
                                .addComponent(btnDeleteService)))
                        .addGap(18, 18, 18)
                        .addComponent(btnClearService))
                    .addComponent(txtServicePrice, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtServiceName, javax.swing.GroupLayout.PREFERRED_SIZE, 648, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 32, Short.MAX_VALUE))
        );
        panelFormServiceLayout.setVerticalGroup(
            panelFormServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormServiceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFormServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServiceName)
                    .addComponent(txtServiceName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(panelFormServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServicePrice)
                    .addComponent(txtServicePrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(panelFormServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServiceUnit)
                    .addComponent(cmbServiceUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelFormServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddService)
                    .addComponent(lblServiceAction)
                    .addComponent(btnUpdateService)
                    .addComponent(btnDeleteService)
                    .addComponent(btnClearService))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        tblServices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Nama Layanan", "Harga", "Satuan"
            }
        ));
        tblServices.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblServicesMouseClicked(evt);
            }
        });
        scrollPaneServices.setViewportView(tblServices);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelFormService, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(scrollPaneServices, javax.swing.GroupLayout.PREFERRED_SIZE, 684, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFormService, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneServices, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 19, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtServiceNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtServiceNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtServiceNameActionPerformed

    private void btnAddServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddServiceActionPerformed
        // TODO add your handling code here:
        handleAdd();
    }//GEN-LAST:event_btnAddServiceActionPerformed

    private void btnUpdateServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateServiceActionPerformed
        // TODO add your handling code here:
        handleUpdate();
    }//GEN-LAST:event_btnUpdateServiceActionPerformed

    private void btnDeleteServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteServiceActionPerformed
        // TODO add your handling code here:
        handleDelete();
    }//GEN-LAST:event_btnDeleteServiceActionPerformed

    private void btnClearServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearServiceActionPerformed
        // TODO add your handling code here:
        clearForm();
    }//GEN-LAST:event_btnClearServiceActionPerformed

    private void tblServicesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblServicesMouseClicked
        // TODO add your handling code here:
        int row = tblServices.getSelectedRow();
        if (row != -1) {
            selectedId = Integer.parseInt(tblServices.getValueAt(row, 0).toString());
            txtServiceName.setText(tblServices.getValueAt(row, 1).toString());
            txtServicePrice.setText(tblServices.getValueAt(row, 2).toString());
            cmbServiceUnit.setSelectedItem(tblServices.getValueAt(row, 3).toString());
        }
    }//GEN-LAST:event_tblServicesMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddService;
    private javax.swing.JButton btnClearService;
    private javax.swing.JButton btnDeleteService;
    private javax.swing.JButton btnUpdateService;
    private javax.swing.JComboBox<String> cmbServiceUnit;
    private javax.swing.JLabel lblServiceAction;
    private javax.swing.JLabel lblServiceName;
    private javax.swing.JLabel lblServicePrice;
    private javax.swing.JLabel lblServiceUnit;
    private javax.swing.JPanel panelFormService;
    private javax.swing.JScrollPane scrollPaneServices;
    private javax.swing.JTable tblServices;
    private javax.swing.JTextField txtServiceName;
    private javax.swing.JTextField txtServicePrice;
    // End of variables declaration//GEN-END:variables
}
