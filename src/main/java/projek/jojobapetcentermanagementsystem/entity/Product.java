/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projek.jojobapetcentermanagementsystem.entity;

/**
 *
 * @author Daniel
 */
public class Product extends Item implements Searchable {
    public static final int LOW_STOCK_LIMIT = 5;
    private String category;
    private int stock;

    public Product() {}

    public Product(int id, String name, String category, double price, int stock) {
        super(id, name, price);
        this.category = category;
        this.stock = stock;
    }

    public Product(String name, String category, double price, int stock) {
        super(name, price);
        this.category = category;
        this.stock = stock;
    }

    @Override
    public String getInfo() {
        return getName() + " [" + category + "] - Stok: " + stock;
    }

    @Override
    public String getItemType() { return "PRODUCT"; }

    @Override
    public boolean matches(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return true;
        String k = keyword.toLowerCase();
        return getName().toLowerCase().contains(k) || category.toLowerCase().contains(k);
    }

    public boolean isLowStock() { return stock < LOW_STOCK_LIMIT; }

    public void reduceStock(int qty) {
        if (qty > 0 && stock >= qty) this.stock -= qty;
    }

    public String getCategory() { 
        return category; 
    }
    public void setCategory(String category) { 
        this.category = category; 
    }
    public int getStock() { 
        return stock; 
    }
    public void setStock(int stock) { 
        this.stock = stock; 
    }

    @Override
    public String toString() {
        return getName() + " (Stok: " + stock + ")";
    }
}
