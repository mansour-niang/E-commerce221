package catalogue.exceptions;

 //Levée quand le format du SKU ne respecte pas la règle : 3 lettres majuscules + tiret + 4 à 6 chiffres.
public class InvalidSkuException extends RuntimeException {

    public InvalidSkuException(String value) {
        super("SKU invalide : \"" + value + "\". Format attendu : 3 lettres majuscules, un tiret, puis 4 à 6 chiffres. Ex: TEC-10204");
    }
}
