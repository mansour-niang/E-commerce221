package dakar_tech_wallet;

import java.util.Objects;

/**
 * Value Object représentant un numéro de téléphone sénégalais.
 * Accepts local forms starting with 70|76|77|78 and optional international +221 prefix.
 */
public record PhoneNumber(String value) {

	public PhoneNumber {
		Objects.requireNonNull(value, "phone number is required");
		String normalized = value.replaceAll("\\s+", "");

		// Accepts either +2217XXXXXXXX or 7XXXXXXXX (where XX.. start with allowed prefixes 70,76,77,78)
		String regex = "^(\\+221)?(70|76|77|78)\\d{7}$";
		if (!normalized.matches(regex)) {
			throw new IllegalArgumentException("Numéro de téléphone invalide pour le Sénégal: " + value);
		}

		// canonical form without spaces
		value = normalized;
	}

	@Override
	public String toString() {
		return value;
	}
}


