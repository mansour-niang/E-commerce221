package catalogue;

import catalogue.exceptions.InvalidDiscountException;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entité centrale du catalogue : un Produit.
 *
 * Règles métier :
 *  - L'ID est généré automatiquement (UUID), jamais fourni par l'extérieur.
 *  - Le nom ne peut pas être null ou vide.
 *  - Le SKU ne peut pas être null.
 *  - Le prix ne peut pas être null.
 *  - Aucun setter public : l'objet ne peut être modifié que via des méthodes métier contrôlées.
 */
public class Product {

    // ── Attributs privés ─────────────────────────────────────────────────────────
    private final UUID   id;    // Généré automatiquement, jamais modifiable
    private final SKU    sku;   // Référence entrepôt, jamais modifiable
    private final String name;  // Nom du produit, jamais modifiable
    private       Money  price; // Prix — seul attribut pouvant évoluer, via applyDiscount()

    // ── Constructeur ─────────────────────────────────────────────────────────────

    /**
     * Crée un nouveau produit. L'ID est généré automatiquement.
     *
     * @param sku   Référence entrepôt (ne peut être null).
     * @param name  Nom du produit (ne peut être null ou vide).
     * @param price Prix initial (ne peut être null).
     */
    public Product(SKU sku, String name, Money price) {

        // Validation du SKU
        if (sku == null) {
            throw new IllegalArgumentException("Le SKU ne peut pas être null.");
        }

        // Validation du nom
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom du produit ne peut pas être vide.");
        }

        // Validation du prix
        if (price == null) {
            throw new IllegalArgumentException("Le prix ne peut pas être null.");
        }

        this.id    = UUID.randomUUID(); // Généré ici, jamais exposé en écriture
        this.sku   = sku;
        this.name  = name.trim();       // On retire les espaces superflus
        this.price = price;
    }

    // ── Méthode métier ────────────────────────────────────────────────────────────

    /**
     * Applique une remise en pourcentage sur le prix du produit.
     *
     * @param percentage Pourcentage de remise, doit être compris entre 0.1 et 100 inclus.
     * @throws InvalidDiscountException si le pourcentage est hors plage.
     */
    public void applyDiscount(BigDecimal percentage) {

        if (percentage == null) {
            throw new InvalidDiscountException("Le pourcentage de remise ne peut pas être null.");
        }

        // Borne inférieure : 0.1 (une remise de 0% n'a pas de sens)
        if (percentage.compareTo(new BigDecimal("0.1")) < 0) {
            throw new InvalidDiscountException(
                    "Le pourcentage de remise doit être au moins 0.1. Valeur reçue : " + percentage
            );
        }

        // Borne supérieure : 100 (on ne peut pas vendre à perte via une remise)
        if (percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new InvalidDiscountException(
                    "Le pourcentage de remise ne peut pas dépasser 100. Valeur reçue : " + percentage
            );
        }

        // Délégation du calcul à Money — on reste dans le Value Object, pas de primitive
        this.price = this.price.applyPercentageReduction(percentage);
    }

    // ── Getters (lecture seule, pas de setters) ──────────────────────────────────

    public UUID   getId()    { return id;    }
    public SKU    getSku()   { return sku;   }
    public String getName()  { return name;  }
    public Money  getPrice() { return price; }

    // ── toString ──────────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Product{" +
                "id="    + id    + ", " +
                "sku="   + sku   + ", " +
                "name='" + name  + "', " +
                "price=" + price +
                "}";
    }
}
