package portsim.util;

/**
 * Exception thrown when a ship is requested by a given IMO number but no ship
 * with that IMO number exists.
 * <p>
 * See {@link portsim.ship.Ship#getShipByImoNumber(long)}.
 * @ass2
 */
public class NoSuchShipException extends Exception {
    /**
     * Constructs a new NoSuchShipException with no detail message or cause.
     *
     * @see Exception#Exception()
     * @ass2
     */
    public NoSuchShipException() {
        super();
    }

    /**
     * Constructs a NoSuchShipException that contains a helpful detail message explaining why
     * the exception occurred.
     *
     * @param message detail message
     * @see Exception#Exception(String)
     * @ass2
     */
    public NoSuchShipException(String message) {
        super(message);
    }

    /**
     * Constructs a NoSuchShipException that stores the underlying cause of the exception.
     *
     * @param cause throwable that caused this exception
     * @see Exception#Exception(Throwable)
     * @ass2
     */
    public NoSuchShipException(Throwable cause) {
        super(cause);
    }


    /**
     * Constructs a NoSuchShipException that contains a helpful detail message explaining why
     * the exception occurred and the underlying cause of the exception.
     *
     * @param message detail message
     * @param cause throwable that caused this exception
     * @see Exception#Exception(String, Throwable)
     * @ass2
     */
    public NoSuchShipException(String message, Throwable cause) {
        super(message, cause);
    }
}
