package catalogue;

import catalogue.exceptions.InvalidCurrencyException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Value Object représentant une somme d'argent.
 *
 * Règles métier :
 *  - Le montant ne peut pas être négatif.
 *  - La devise doit être "FCFA" ou "EUR".
 *  - On ne peut additionner que des montants de même devise.
 *
 * Utilise un Record Java : immuable par nature, equals/hashCode/toString générés automatiquement.
 */
public record Money(BigDecimal amount, String currency) {

    // Liste des devises autorisées
    private static final List<String> ALLOWED_CURRENCIES = List.of("FCFA", "EUR");

    /**
     * Constructeur compact du record — s'exécute automatiquement à chaque instanciation.
     * C'est ici que toutes les validations sont appliquées.
     */
    public Money {
        // 1. Vérification que le montant n'est pas null
        if (amount == null) {
            throw new IllegalArgumentException("Le montant ne peut pas être null.");
        }

        // 2. Vérification que le montant n'est pas négatif
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant ne peut pas être négatif : " + amount);
        }

        // 3. Vérification que la devise n'est pas null ou vide
        if (currency == null || currency.isBlank()) {
            throw new InvalidCurrencyException("null ou vide");
        }

        // 4. Vérification que la devise est autorisée
        if (!ALLOWED_CURRENCIES.contains(currency)) {
            throw new InvalidCurrencyException(currency);
        }

        // On normalise le montant à 2 décimales (ex: 1000.5 devient 1000.50)
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Additionne ce montant avec un autre.
     * Les deux montants doivent avoir la même devise, sinon exception.
     *
     * @param other L'autre montant à ajouter.
     * @return Un nouveau Money représentant la somme (immuable).
     */
    public Money add(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Impossible d'additionner avec un montant null.");
        }

        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "Impossible d'additionner des devises différentes : " + this.currency + " et " + other.currency
            );
        }

        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * Applique un pourcentage de réduction et retourne un nouveau Money réduit.
     * Utilisé par Product.applyDiscount().
     *
     * @param percentage Pourcentage entre 0.1 et 100.
     * @return Un nouveau Money avec le prix réduit.
     */
    public Money applyPercentageReduction(BigDecimal percentage) {
        // Calcul : montant * (1 - percentage/100)
        BigDecimal factor = BigDecimal.ONE.subtract(
                percentage.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
        );
        BigDecimal newAmount = this.amount.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        return new Money(newAmount, this.currency);
    }

    /**
     * Affichage lisible : ex. "1 500.00 FCFA"
     */
    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
