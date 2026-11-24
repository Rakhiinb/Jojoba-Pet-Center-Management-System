package projek.jojobapetcentermanagementsystem.repository;

import projek.jojobapetcentermanagementsystem.entity.Product;
import projek.jojobapetcentermanagementsystem.model.DashboardModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardRepository {
    public DashboardModel getSummary() {
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM customers) AS total_customers, "
                + "(SELECT COUNT(*) FROM pets) AS total_pets, "
                + "(SELECT COUNT(*) FROM products) AS total_products, "
                + "(SELECT COUNT(*) FROM products WHERE stock < ?) AS low_stock, "
                + "(SELECT COUNT(*) FROM transactions "
                + "   WHERE transaction_date >= CURDATE() "
                + "     AND transaction_date < CURDATE() + INTERVAL 1 DAY) AS today_transactions, "
                + "(SELECT COALESCE(SUM(total), 0) FROM transactions "
                + "   WHERE transaction_date >= CURDATE() "
                + "     AND transaction_date < CURDATE() + INTERVAL 1 DAY) AS today_revenue";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Product.LOW_STOCK_LIMIT);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DashboardModel(
                            rs.getInt("total_customers"),
                            rs.getInt("total_pets"),
                            rs.getInt("total_products"),
                            rs.getInt("low_stock"),
                            rs.getInt("today_transactions"),
                            rs.getDouble("today_revenue"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new DashboardModel(0, 0, 0, 0, 0, 0); // gagal: tampilkan nol, jangan crash
    }
}
