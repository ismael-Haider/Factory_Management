package FactoryManagement.models;

import java.util.Objects;

public class FinishedProduct {
    private int productId;
    private String name;
    private int quantity;

    public FinishedProduct(int productId, String name, int quantity) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void reduceQuantity(int quantity) {
        this.quantity -= quantity;
    }

    public String toCSV() {
        return productId + "," + name + "," + quantity;
    }

    public static FinishedProduct fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        return new FinishedProduct(Integer.parseInt(parts[0]), parts[1], Integer.parseInt(parts[2]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FinishedProduct that = (FinishedProduct) o;
        return productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return "FinishedProduct{" + "productId=" + productId + ", name='" + name + '\'' + ", quantity=" + quantity
                + '}';
    }
}