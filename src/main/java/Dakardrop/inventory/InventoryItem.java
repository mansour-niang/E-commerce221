package Dakardrop.inventory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Représente un enregistrement de stock en dépôt : produit + quantité.
 */
public final class InventoryItem {
    private final Product product;
    private final AtomicInteger quantity;

    public InventoryItem(Product product, int initialQuantity) {
        if (product == null) throw new IllegalArgumentException("Product obligatoire");
        if (initialQuantity < 0) throw new IllegalArgumentException("Quantité initiale ne peut pas être négative");
        this.product = product;
        this.quantity = new AtomicInteger(initialQuantity);
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity.get();
    }

    void increase(int delta) {
        if (delta <= 0) throw new IllegalArgumentException("Delta doit être > 0");
        quantity.addAndGet(delta);
    }

    void decrease(int delta) {
        if (delta <= 0) throw new IllegalArgumentException("Delta doit être > 0");
        int prev;
        int next;
        do {
            prev = quantity.get();
            next = prev - delta;
            if (next < 0) throw new IllegalArgumentException("Opération rendrait la quantité négative");
        } while (!quantity.compareAndSet(prev, next));
    }

    @Override
    public String toString() {
        return product + " qty=" + getQuantity();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryItem that)) return false;
        return Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product);
    }
}


