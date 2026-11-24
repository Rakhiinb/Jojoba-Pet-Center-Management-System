package projek.jojobapetcentermanagementsystem.model;

/** Angka-angka ringkasan untuk kartu di Dashboard (hasil query agregat). */
public class DashboardModel {

    private final int totalCustomers;
    private final int totalPets;
    private final int totalProducts;
    private final int lowStockCount;
    private final int todayTransactions;
    private final double todayRevenue;

    public DashboardModel(int totalCustomers, int totalPets, int totalProducts,
        int lowStockCount, int todayTransactions, double todayRevenue) {
        this.totalCustomers = totalCustomers;
        this.totalPets = totalPets;
        this.totalProducts = totalProducts;
        this.lowStockCount = lowStockCount;
        this.todayTransactions = todayTransactions;
        this.todayRevenue = todayRevenue;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public int getTotalPets() {
        return totalPets;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public int getLowStockCount() {
        return lowStockCount;
    }

    public int getTodayTransactions() {
        return todayTransactions;
    }

    public double getTodayRevenue() {
        return todayRevenue;
    }
}
