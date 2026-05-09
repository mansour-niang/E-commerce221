package dakar_tech_wallet;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object représentant une somme d'argent dans une devise donnée.
 * Immutable by design (record).
 */
public record Money(BigDecimal amount, String currency) {

    public Money {
        Objects.requireNonNull(amount, "amount is required");
        Objects.requireNonNull(currency, "currency is required");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif");
        }

        if (currency.isBlank()) {
            throw new IllegalArgumentException("La devise ne peut être vide");
        }

        // normalize currency to upper-case ISO-like form
        currency = currency.trim().toUpperCase();
    }

    public Money add(Money other) {
        Objects.requireNonNull(other, "other is required");
        if (!this.currency.equalsIgnoreCase(other.currency)) {
            throw new IllegalArgumentException("Impossible d'additionner des Money de devises différentes");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}

