package catalogue;

import catalogue.exceptions.InvalidSkuException;

/**
 * Value Object représentant la référence unique d'un produit en entrepôt (SKU).
 *
 * Format strict : 3 lettres MAJUSCULES + tiret + 4 à 6 chiffres.
 * Exemples valides   : TEC-10204 / PHO-999999 / LIV-1234
 * Exemples invalides : tec-10204 (minuscules) / TEC-123 (trop peu de chiffres) / TECH-1234 (4 lettres)
 */
public record SKU(String value) {

    // Expression régulière : ^ début, [A-Z]{3} exactement 3 majuscules,
    // - tiret littéral, \d{4,6} entre 4 et 6 chiffres, $ fin.
    private static final String SKU_PATTERN = "^[A-Z]{3}-\\d{4,6}$";

    /**
     * Constructeur compact — validé à chaque instanciation.
     */
    public SKU {
        if (value == null || value.isBlank()) {
            throw new InvalidSkuException("null ou vide");
        }

        if (!value.matches(SKU_PATTERN)) {
            throw new InvalidSkuException(value);
        }
    }

    /**
     * Affichage lisible : ex. "TEC-10204"
     */
    @Override
    public String toString() {
        return value;
    }
}
