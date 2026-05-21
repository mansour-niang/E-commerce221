package Dakardrop.exceptions;

/**
 * Base exception for inventory-related errors.
 */
public class InventoryException extends RuntimeException {
    public InventoryException(String message) {
        super(message);
    }

    public InventoryException(String message, Throwable cause) {
        super(message, cause);
    }
}


