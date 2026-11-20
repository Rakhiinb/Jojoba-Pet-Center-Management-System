package projek.jojobapetcentermanagementsystem.repository;

import projek.jojobapetcentermanagementsystem.entity.Transaction;
import projek.jojobapetcentermanagementsystem.entity.TransactionItem;
import projek.jojobapetcentermanagementsystem.model.TransactionModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Akses database transaksi: simpan atomik dan query riwayat. */
public class TransactionRepository {

    /**
     * Menyimpan header, item, dan potongan stok dalam satu transaksi database.
     * Semua perubahan di-rollback bila ada item gagal, misalnya stok tidak cukup.
     */
    public boolean save(Transaction transaction) {
        String transactionSql = "INSERT INTO transactions (customer_id, pet_id, transaction_date, notes, total) "
                + "VALUES (?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO transaction_items "
                + "(transaction_id, item_type, product_id, service_id, description, unit_price, quantity, subtotal) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String stockSql = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int transactionId;
                try (PreparedStatement ps = conn.prepareStatement(transactionSql, Statement.RETURN_GENERATED_KEYS)) {
                    setNullableInt(ps, 1, transaction.getCustomerId());
                    setNullableInt(ps, 2, transaction.getPetId());
                    LocalDateTime date = (transaction.getDate() != null)
                            ? transaction.getDate()
                            : LocalDateTime.now();
                    ps.setTimestamp(3, Timestamp.valueOf(date));
                    ps.setString(4, transaction.getNotes());
                    ps.setDouble(5, transaction.calculateTotal());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Gagal mendapatkan id transaksi");
                        }
                        transactionId = keys.getInt(1);
                    }
                }

                if (transaction.getItems() != null) {
                    for (TransactionItem item : transaction.getItems()) {
                        boolean isProduct = "PRODUCT".equalsIgnoreCase(item.getItemType());
                        try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                            ps.setInt(1, transactionId);
                            ps.setString(2, item.getItemType());
                            if (isProduct) {
                                ps.setInt(3, item.getItemId());
                                ps.setNull(4, Types.INTEGER);
                            } else {
                                ps.setNull(3, Types.INTEGER);
                                ps.setInt(4, item.getItemId());
                            }
                            ps.setString(5, item.getDescription());
                            ps.setDouble(6, item.getUnitPrice());
                            ps.setInt(7, item.getQuantity());
                            ps.setDouble(8, item.getSubtotal());
                            ps.executeUpdate();
                        }
                        if (isProduct) {
                            try (PreparedStatement ps = conn.prepareStatement(stockSql)) {
                                ps.setInt(1, item.getQuantity());
                                ps.setInt(2, item.getItemId());
                                ps.setInt(3, item.getQuantity());
                                if (ps.executeUpdate() == 0) {
                                    throw new SQLException("Stok produk " + item.getItemId() + " tidak cukup");
                                }
                            }
                        }
                    }
                }

                conn.commit();
                transaction.setId(transactionId);
                return true;
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    rollbackError.printStackTrace();
                }
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Riwayat transaksi dalam rentang tanggal; batas kosong memakai default. */
    public List<TransactionModel> findByDateRange(LocalDate from, LocalDate to) {
        List<TransactionModel> result = new ArrayList<>();
        String sql = "SELECT t.id, t.transaction_date, COALESCE(c.name, 'Umum') AS customer_name, "
                + "COALESCE(p.name, '-') AS pet_name, t.total "
                + "FROM transactions t "
                + "LEFT JOIN customers c ON t.customer_id = c.id "
                + "LEFT JOIN pets p ON t.pet_id = p.id "
                + "WHERE t.transaction_date >= ? AND t.transaction_date < ? "
                + "ORDER BY t.transaction_date DESC, t.id DESC";
        LocalDate start = (from != null) ? from : LocalDate.of(1900, 1, 1);
        LocalDate endExclusive = (to != null ? to : LocalDate.now()).plusDays(1);
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(endExclusive.atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new TransactionModel(
                            rs.getInt("id"),
                            rs.getTimestamp("transaction_date").toLocalDateTime(),
                            rs.getString("customer_name"),
                            rs.getString("pet_name"),
                            rs.getDouble("total")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /** Item-item milik satu transaksi, diurutkan berdasarkan id. */
    public List<TransactionItem> findItems(int transactionId) {
        List<TransactionItem> result = new ArrayList<>();
        String sql = "SELECT id, item_type, product_id, service_id, description, unit_price, quantity "
                + "FROM transaction_items WHERE transaction_id = ? ORDER BY id";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("item_type");
                    int itemId = "PRODUCT".equalsIgnoreCase(type)
                            ? rs.getInt("product_id")
                            : rs.getInt("service_id");
                    TransactionItem item = new TransactionItem(
                            type,
                            itemId,
                            rs.getString("description"),
                            rs.getDouble("unit_price"),
                            rs.getInt("quantity"));
                    item.setId(rs.getInt("id"));
                    result.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /** Menulis NULL untuk foreign key opsional (customer_id / pet_id). */
    private static void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }
}
