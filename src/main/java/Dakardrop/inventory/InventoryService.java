package Dakardrop.inventory;

import Dakardrop.exceptions.InsufficientStockException;
import Dakardrop.exceptions.InvalidProductException;
import Dakardrop.model.ProductStockView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service thread-safe pour gérer le stock en mémoire.
 * - createProduct
 * - addStock
 * - removeStock
 * - getStock
 * - listLowStock
 */
public class InventoryService {
    private final Map<SKU, InventoryItem> items = new ConcurrentHashMap<>();

    /**
     * Enregistre un produit et initialise la quantité en dépôt.
     */
    public void createProduct(Product product, int initialQuantity) {
        if (product == null) throw new InvalidProductException("Product obligatoire");
        if (initialQuantity < 0) throw new InvalidProductException("Quantité initiale ne peut pas être négative");
        InventoryItem item = new InventoryItem(product, initialQuantity);
        InventoryItem prev = items.putIfAbsent(product.getSku(), item);
        if (prev != null) {
            throw new InvalidProductException("Produit déjà enregistré : " + product.getSku());
        }
    }

    /**
     * Ajoute du stock pour un SKU existant.
     */
    public void addStock(SKU sku, int quantity) {
        requireSku(sku);
        if (quantity <= 0) throw new IllegalArgumentException("Quantity doit être > 0");
        InventoryItem item = items.get(sku);
        if (item == null) throw new InvalidProductException("SKU inconnu : " + sku);
        item.increase(quantity);
    }

    /**
     * Retire du stock pour une vente ou perte. Garantit que le stock ne devient jamais négatif.
     */
    public void removeStock(SKU sku, int quantity) throws InsufficientStockException {
        requireSku(sku);
        if (quantity <= 0) throw new IllegalArgumentException("Quantity doit être > 0");
        InventoryItem item = items.get(sku);
        if (item == null) throw new InvalidProductException("SKU inconnu : " + sku);
        synchronized (item) {
            int current = item.getQuantity();
            if (current < quantity) {
                throw new InsufficientStockException("Stock insuffisant pour " + sku + ": demandé=" + quantity + " disponible=" + current);
            }
            item.decrease(quantity);
        }
    }

    public int getStock(SKU sku) {
        requireSku(sku);
        InventoryItem item = items.get(sku);
        if (item == null) throw new InvalidProductException("SKU inconnu : " + sku);
        return item.getQuantity();
    }

    public List<ProductStockView> listLowStock(int threshold) {
        if (threshold < 0) throw new IllegalArgumentException("Threshold doit être >= 0");
        return items.values().stream()
                .filter(item -> item.getQuantity() < threshold)
                .map(ProductStockView::from)
                .sorted((a, b) -> a.getSku().getValue().compareTo(b.getSku().getValue()))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Alias plus explicite côté métier : retourne les produits sous le seuil critique.
     */
    public List<ProductStockView> listLowStockViews(int threshold) {
        return listLowStock(threshold);
    }

    /**
     * Retourne tous les items de l'inventaire sous forme de vue (pour sérialisation).
     */
    public List<ProductStockView> getAllItems() {
        return items.values().stream()
                .map(ProductStockView::from)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Retourne le nombre de produits différents en stock.
     */
    public int getProductCount() {
        return items.size();
    }

    private void requireSku(SKU sku) {
        if (sku == null) throw new IllegalArgumentException("SKU obligatoire");
    }
}


