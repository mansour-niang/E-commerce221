package Dakardrop.exceptions;

/**
 * Raised when a product is invalid (null fields, bad SKU, etc.).
 */
public class InvalidProductException extends InventoryException {
    public InvalidProductException(String message) {
        super(message);
    }
}


