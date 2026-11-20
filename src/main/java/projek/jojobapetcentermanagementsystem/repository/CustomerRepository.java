package projek.jojobapetcentermanagementsystem.repository;

import projek.jojobapetcentermanagementsystem.entity.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Semua SQL untuk tabel customers. */
public class CustomerRepository implements CrudRepository<Customer> {

    private static final String SELECT = "SELECT id, name, phone, address FROM customers";

    @Override
    public List<Customer> findAll() {
        List<Customer> result = new ArrayList<>();
        String sql = SELECT + " ORDER BY name";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Customer findById(int id) {
        String sql = SELECT + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Cari customer berdasarkan nama, telepon, atau alamat (mengandung kata kunci). */
    public List<Customer> search(String keyword) {
        List<Customer> result = new ArrayList<>();
        String sql = SELECT + " WHERE name LIKE ? OR phone LIKE ? OR address LIKE ? ORDER BY name";
        String like = "%" + keyword.trim() + "%";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /** Menyimpan customer baru; bila berhasil, id hasil auto-increment diisi ke objek. */
    @Override
    public boolean save(Customer customer) {
        String sql = "INSERT INTO customers (name, phone, address) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getAddress());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        customer.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Customer customer) {
        String sql = "UPDATE customers SET name = ?, phone = ?, address = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getAddress());
            ps.setInt(4, customer.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** @return false bila gagal, termasuk bila customer masih punya hewan atau transaksi (FK). */
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            // Data masih dipakai tabel lain. Ini kasus normal, bukan error program,
            // jadi cukup return false dan biarkan view menampilkan pesannya.
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("address"));
    }
}
