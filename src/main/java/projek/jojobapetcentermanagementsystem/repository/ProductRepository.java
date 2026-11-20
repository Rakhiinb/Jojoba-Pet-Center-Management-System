/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projek.jojobapetcentermanagementsystem.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import projek.jojobapetcentermanagementsystem.entity.Product;

/**
 *
 * @author Daniel
 */
public class ProductRepository implements CrudRepository<Product> {
    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getDouble("price"),
            rs.getInt("stock")
        );
    }

    @Override
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY name ASC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Product findById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean save(Product p) {
        String sql = "INSERT INTO products (name, category, price, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStock());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Product p) {
        String sql = "UPDATE products SET name = ?, category = ?, price = ?, stock = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Overload 1: cari berdasarkan keyword saja
    public List<Product> search(String keyword) {
        return search(keyword, "Semua");
    }

    // Overload 2: cari berdasarkan keyword dan kategori
    public List<Product> search(String keyword, String category) {
        List<Product> list = new ArrayList<>();
        boolean filterCategory = category != null && !category.equalsIgnoreCase("Semua") && !category.isEmpty();
        String sql = filterCategory
                ? "SELECT * FROM products WHERE (name LIKE ? OR category LIKE ?) AND category = ? ORDER BY name ASC"
                : "SELECT * FROM products WHERE (name LIKE ? OR category LIKE ?) ORDER BY name ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + (keyword == null ? "" : keyword.trim()) + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            if (filterCategory) {
                ps.setString(3, category);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Dipakai juga oleh DashboardPanel Orang 1
    public List<Product> findLowStock() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE stock < ? ORDER BY stock ASC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Product.LOW_STOCK_LIMIT);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> findCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM products ORDER BY category ASC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }
}
