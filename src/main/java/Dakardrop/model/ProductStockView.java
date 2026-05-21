package Dakardrop.model;

import Dakardrop.inventory.InventoryItem;
import Dakardrop.inventory.Money;
import Dakardrop.inventory.Product;
import Dakardrop.inventory.SKU;

import java.util.Objects;

/**
 * Vue métier immuable d'un produit avec sa quantité en stock.
 */
public final class ProductStockView {
    private final SKU sku;
    private final String name;
    private final String description;
    private final Money unitPrice;
    private final int quantity;

    public ProductStockView(SKU sku, String name, String description, Money unitPrice, int quantity) {
        this.sku = Objects.requireNonNull(sku, "SKU obligatoire");
        this.name = requireText(name, "Nom obligatoire");
        this.description = description == null ? "" : description.trim();
        this.unitPrice = Objects.requireNonNull(unitPrice, "Prix unitaire obligatoire");
        if (quantity < 0) throw new IllegalArgumentException("Quantité ne peut pas être négative");
        this.quantity = quantity;
    }

    public static ProductStockView from(InventoryItem item) {
        if (item == null) throw new IllegalArgumentException("InventoryItem obligatoire");
        Product product = item.getProduct();
        return new ProductStockView(
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getUnitPrice(),
                item.getQuantity()
        );
    }

    public SKU getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isLowStock(int threshold) {
        return quantity < threshold;
    }

    @Override
    public String toString() {
        return "ProductStockView{" +
                "sku=" + sku +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductStockView that)) return false;
        return quantity == that.quantity &&
                Objects.equals(sku, that.sku) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(unitPrice, that.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku, name, description, unitPrice, quantity);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}

