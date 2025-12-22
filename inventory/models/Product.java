package inventory.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Product {
    static int counter=0;
    private int id;
    private String name;
    private Map<Integer, Integer> itemQuantities; // Item ID -> quantity needed

    public Product(String name, Map<Integer, Integer> itemQuantities) {
        counter+=1;
        this.id = counter;
        this.name = name;
        this.itemQuantities = new HashMap<>(itemQuantities);
    }

    // For loading from CSV (itemQuantities as "itemId:qty;itemId:qty")
    public Product(int id, String name, String itemQuantitiesStr) {
        counter=id;
        this.id = id;
        this.name = name;
        this.itemQuantities = parseItemQuantities(itemQuantitiesStr);
    }

    private Map<Integer, Integer> parseItemQuantities(String str) {
        Map<Integer, Integer> map = new HashMap<>();
        if (str.isEmpty()) return map;
        for (String pair : str.split(";")) {
            String[] parts = pair.split(":");
            map.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
        return map;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public HashMap<Integer, Integer> getItemQuantities() { return new HashMap<>(itemQuantities); }
    public void setItemQuantities(Map<Integer, Integer> itemQuantities) { this.itemQuantities = new HashMap<>(itemQuantities); }

    // CSV Serialization
    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : itemQuantities.entrySet()) {
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 1); // Remove last ;
        return id + "," + name + "," + sb.toString();
    }

    public static Product fromCSV(String csvLine) {
        // عملنا شي
        String[] parts = csvLine.split(",");
        return new Product(Integer.parseInt(parts[0]), parts[1], parts.length > 2 ? parts[2] : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", name='" + name + '\'' + ", itemQuantities=" + itemQuantities + '}';
    }
}