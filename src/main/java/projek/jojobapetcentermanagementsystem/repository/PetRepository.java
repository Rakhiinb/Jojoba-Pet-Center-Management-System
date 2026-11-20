package projek.jojobapetcentermanagementsystem.repository;

import projek.jojobapetcentermanagementsystem.entity.Pet;
import projek.jojobapetcentermanagementsystem.model.PetModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Semua SQL untuk tabel pets. */
public class PetRepository implements CrudRepository<Pet> {

    private static final String SELECT =
            "SELECT id, customer_id, name, species, breed, gender FROM pets";

    @Override
    public List<Pet> findAll() {
        List<Pet> result = new ArrayList<>();
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
    public Pet findById(int id) {
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

    /** Hewan milik satu customer (untuk tabel detail di CustomerPetPanel dan dropdown Kasir). */
    public List<Pet> findByCustomer(int customerId) {
        List<Pet> result = new ArrayList<>();
        String sql = SELECT + " WHERE customer_id = ? ORDER BY name";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
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

    /** Semua hewan beserta nama pemiliknya (JOIN pets dan customers). */
    public List<PetModel> findAllWithOwner() {
        List<PetModel> result = new ArrayList<>();
        String sql = "SELECT p.id, p.name, p.species, p.breed, p.gender, c.name AS owner_name "
                + "FROM pets p JOIN customers c ON p.customer_id = c.id "
                + "ORDER BY p.name";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new PetModel(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("species"),
                        rs.getString("breed"),
                        rs.getString("gender"),
                        rs.getString("owner_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /** Menyimpan hewan baru; bila berhasil, id hasil auto-increment diisi ke objek. */
    @Override
    public boolean save(Pet pet) {
        String sql = "INSERT INTO pets (customer_id, name, species, breed, gender) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pet.getCustomerId());
            ps.setString(2, pet.getName());
            ps.setString(3, pet.getSpecies());
            ps.setString(4, pet.getBreed());
            ps.setString(5, pet.getGender());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        pet.setId(keys.getInt(1));
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
    public boolean update(Pet pet) {
        String sql = "UPDATE pets SET name = ?, species = ?, breed = ?, gender = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pet.getName());
            ps.setString(2, pet.getSpecies());
            ps.setString(3, pet.getBreed());
            ps.setString(4, pet.getGender());
            ps.setInt(5, pet.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** @return false bila gagal, termasuk bila hewan sudah tercatat di transaksi (FK). */
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM pets WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false; // masih dipakai transaksi; view menampilkan pesannya
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Pet mapRow(ResultSet rs) throws SQLException {
        return new Pet(
                rs.getInt("id"),
                rs.getInt("customer_id"),
                rs.getString("name"),
                rs.getString("species"),
                rs.getString("breed"),
                rs.getString("gender"));
    }
}
