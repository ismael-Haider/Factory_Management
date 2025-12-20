package inventory.models;

import java.util.Objects;

public class Item {
    static int counter=0;
    private int id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private int minQuantity;

    public Item(String name, String category, double price, int quantity, int minQuantity) {
        counter+=1;
        System.out.println(counter);
        this.id = inventory.utils.IdGenerator.generateId(Item.class,counter);
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
    }

    // For loading from CSV
    public Item(int id, String name, String category, double price, int quantity, int minQuantity) {
        counter=id;
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void addQuantity(int quantity) {this.quantity+=quantity;}
    public void reduceQuantity(int quantity) {this.quantity-=quantity;}
    public int getMinQuantity() { return minQuantity; }
    public void setMinQuantity(int minQuantity) { this.minQuantity = minQuantity; }

    // CSV Serialization
    public String toCSV() {
        return id + "," + name + "," + category + "," + price + "," + quantity + "," + minQuantity;
    }

    public static Item fromCSV(String csvLine) {
        System.out.println(csvLine);
        String[] parts = csvLine.split(",");
        return new Item(Integer.parseInt(parts[0]), parts[1], parts[2], Double.parseDouble(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id;
    }

    public boolean isLow(){
        return quantity<=minQuantity;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Item{" + "id=" + id + ", name='" + name + '\'' + ", category='" + category + '\'' + ", price=" + price + ", quantity=" + quantity + ", minQuantity=" + minQuantity + '}';
    }
}