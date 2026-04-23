package catalogue.exceptions;

 // Levée quand une devise non supportée est utilisée (seules "FCFA" et "EUR" sont acceptées).
public class InvalidCurrencyException extends RuntimeException {

    public InvalidCurrencyException(String currency) {
        super("Devise invalide : \"" + currency + "\". Seules FCFA et EUR sont acceptées.");
    }
}
