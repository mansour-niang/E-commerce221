package Dakardrop.inventory;

import Dakardrop.exceptions.InvalidProductException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Simple money value object for prices in XOF/FCFA.
 * Immuable et vérifie que le montant est >= 0 et que la devise est supportée.
 */
public final class Money {
    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency) {
        if (amount == null) throw new InvalidProductException("Montant ne peut pas être null");
        if (currency == null) throw new InvalidProductException("Devise ne peut pas être null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new InvalidProductException("Montant doit être strictement positif (> 0)");
        String cur = currency.trim().toUpperCase();
        if (!cur.equals("XOF") && !cur.equals("FCFA")) {
            throw new InvalidProductException("Devise non supportée : attendre XOF/FCFA");
        }
        this.amount = amount;
        this.currency = cur;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.add(other.amount), currency);
    }

    private void requireSameCurrency(Money other) {
        if (other == null) throw new InvalidProductException("Money à ajouter ne peut pas être null");
        if (!this.currency.equals(other.currency)) {
            throw new InvalidProductException("Devise différente : " + this.currency + " vs " + other.currency);
        }
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}



