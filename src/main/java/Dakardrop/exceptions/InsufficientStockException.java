package Dakardrop.exceptions;

/**
 * Raised when an operation would cause stock to go negative.
 */
public class InsufficientStockException extends InventoryException {
    public InsufficientStockException(String message) {
        super(message);
    }
}


