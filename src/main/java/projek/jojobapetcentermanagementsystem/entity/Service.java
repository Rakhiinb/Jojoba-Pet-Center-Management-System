/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projek.jojobapetcentermanagementsystem.entity;

/**
 *
 * @author Daniel
 */
public class Service extends Item {
    private String unit;

    public Service() {}

    public Service(int id, String name, double price, String unit) {
        super(id, name, price);
        this.unit = unit;
    }

    public Service(String name, double price, String unit) {
        super(name, price);
        this.unit = unit;
    }

    @Override
    public String getInfo() {
        return getName() + " (per " + unit + ")";
    }

    @Override
    public String getItemType() { return "SERVICE"; }

    @Override
    public String getItemName() {
        return getName() + " (/" + unit + ")";
    }

    public String getUnit() { 
        return unit; 
    }
    public void setUnit(String unit) { 
        this.unit = unit; 
    }

    @Override
    public String toString() {
        return getItemName();
    }
}
