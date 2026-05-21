package Dakardrop.inventory;

import Dakardrop.exceptions.InvalidProductException;

import java.util.Objects;

/**
 * Produit vendable par DakarDrop. Immutable après création.
 */
public final class Product {
    private final SKU sku;
    private final String name;
    private final String description;
    private final Money unitPrice;

    public Product(SKU sku, String name, String description, Money unitPrice) {
        if (sku == null) throw new InvalidProductException("SKU obligatoire");
        if (name == null || name.trim().isEmpty()) throw new InvalidProductException("Nom du produit obligatoire");
        String trimmedName = name.trim();
        if (!trimmedName.matches("[a-zA-ZÀ-ÿ\\s-]+")) {
            throw new InvalidProductException("Nom du produit doit contenir uniquement des lettres, espaces ou tirets");
        }
        if (unitPrice == null) throw new InvalidProductException("Prix unitaire obligatoire");
        this.sku = sku;
        this.name = trimmedName;
        this.description = description == null ? "" : description.trim();
        this.unitPrice = unitPrice;
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

    @Override
    public String toString() {
        return "Product{" + sku + ": '" + name + "' - " + unitPrice + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(sku, product.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }
}



