package model;

public record Product(String name, double price) {
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

