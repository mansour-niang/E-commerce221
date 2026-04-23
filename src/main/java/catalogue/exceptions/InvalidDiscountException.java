package catalogue.exceptions;

 //Levée quand le pourcentage de remise est hors de la plage autorisée (0.1 à 100).
public class InvalidDiscountException extends RuntimeException {

    public InvalidDiscountException(String message) {
        super(message);
    }
}
