/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projek.jojobapetcentermanagementsystem.entity;

/**
 *
 * @author Daniel
 */
public abstract class Item extends BaseEntity implements Billable {
    private String name;
    private double price;

    public Item() {}

    public Item(int id, String name, double price) {
        setId(id);
        this.name = name;
        this.price = price;
    }

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public abstract String getInfo();

    @Override
    public int getItemId() { 
        return getId(); 
    }

    @Override
    public String getItemName() { 
        return name; 
    }

    @Override
    public double getPrice() { 
        return price; 
    }

    public String getName() { 
        return name; 
    }
    public void setName(String name) { 
        this.name = name; 
    }
    public void setPrice(double price) { 
        this.price = price; 
    }
}
