package Dakardrop.inventory;

import Dakardrop.exceptions.InvalidProductException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object that represents a SKU. Very strict validation to avoid bad references.
 */
public final class SKU {
    private static final Pattern PATTERN = Pattern.compile("[A-Z]{3}-\\d{4,6}");

    private final String value;

    public SKU(String value) {
        if (value == null) throw new InvalidProductException("SKU ne peut pas être null");
        String v = value.trim();
        if (!PATTERN.matcher(v).matches()) {
            throw new InvalidProductException("SKU invalide : doit respecter le format AAA-1234 (lettres majuscules)");
        }
        this.value = v;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SKU)) return false;
        SKU sku = (SKU) o;
        return Objects.equals(value, sku.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}



