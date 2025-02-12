package portsim.movement;

import portsim.ship.Ship;
import portsim.util.BadEncodingException;
import portsim.util.NoSuchShipException;

/**
 * The movement of a ship coming into or out of the port.
 *
 * @ass1_partial
 */
public class ShipMovement extends Movement {

    /**
     * The ship entering of leaving the Port
     */
    private Ship ship;

    /**
     * Creates a new ship movement with the given action time and direction
     * to be undertaken with the given ship.
     *
     * @param time      the time the movement should occur
     * @param direction the direction of the movement
     * @param ship      the ship which that is waiting to move
     * @throws IllegalArgumentException if time &lt; 0
     * @ass1
     */
    public ShipMovement(long time, MovementDirection direction, Ship ship)
        throws IllegalArgumentException {
        super(time, direction);
        this.ship = ship;
    }

    /**
     * Returns the ship undertaking the movement.
     *
     * @return movements ship
     * @ass1
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Returns the human-readable string representation of this ShipMovement.
     * <p>
     * The format of the string to return is
     * <pre>
     * DIRECTION ShipMovement to occur at time involving the ship name </pre>
     * Where:
     * <ul>
     *   <li>{@code DIRECTION} is the direction of the movement </li>
     *   <li>{@code time} is the time the movement is meant to occur </li>
     *   <li>{@code name} is the name of the ship that is being moved</li>
     * </ul>
     * For example:
     * <pre>
     * OUTBOUND ShipMovement to occur at 135 involving the ship Voyager </pre>
     *
     * @return string representation of this ShipMovement
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s involving the ship %s",
            super.toString(),
            this.ship.getName());
    }

    /**
     * Returns the machine-readable string representation of this ship movement.
     * <p>
     * The format of the string to return is
     * <pre>ShipMovement:time:direction:imoNumber</pre>
     * Where:
     *  <ul>
     *   <li>{@code time} is the time that the movement will be actioned</li>
     *   <li>{@code direction} is the direction of the movement</li>
     *   <li>{@code imoNumber} is the imoNumber of the ship that is moving</li>
     * </ul>
     * For example:
     *
     * <pre>ShipMovement:120:INBOUND:1258691</pre>
     *
     * @return encoded string representation of this movement
     * @ass2
     */
    @Override
    public String encode() {
        return String.format("%s:%s",
            super.encode(),
            this.ship.getImoNumber());
    }

    /**
     * Creates a ship movement from a string encoding.
     * <p>
     * The format of the string should match the encoded representation of a
     * ship movement, as described in {@link ShipMovement#encode()}.
     * <p>
     * The encoded string is invalid if any of the following conditions are true:
     * <ul>
     * <li>The number of colons ({@code :}) detected was more/fewer than expected.</li>
     * <li>The time is not a long (i.e. cannot be parsed by
     * {@link Long#parseLong(String)}).</li>
     * <li>The time is less than zero (0).</li>
     * <li>The movementDirection is not one of the valid directions (See
     * {@link MovementDirection}.</li>
     * <li>The imoNumber is not a long (i.e. cannot be parsed by
     * {@link Long#parseLong(String)}).</li>
     * <li>There is no ship that exists with the specified imoNumber.</li>
     * </ul>
     *
     * @param string string containing the encoded ShipMovement
     * @return decoded ShipMovement instance
     * @throws BadEncodingException if the format of the given string is invalid according to
     *                              the rules above
     * @ass2
     */
    public static ShipMovement fromString(String string) throws BadEncodingException {

        String[] parts = string.split(":", -1);
        if (parts.length != 4) {
            throw new BadEncodingException("Invalid movement, was expecting 4"
                + " parts: " + string);
        }
        if (!parts[0].equals("ShipMovement")) {
            throw new BadEncodingException("Expected a ShipMovement encoding:"
                + " " + string);
        }

        long time;
        try {
            time = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("Time must be a valid long: " + parts[1], e);
        }
        if (time < 0) {
            throw new BadEncodingException("Time must be at greater than or "
                + "equal to 0: " + parts[1]);
        }
        MovementDirection direction;
        try {
            direction = MovementDirection.valueOf(parts[2]);
        } catch (IllegalArgumentException e) {
            throw new BadEncodingException("Invalid direction in encoding: " + parts[2], e);
        }
        long imoNumber;
        try {
            imoNumber = Long.parseLong(parts[3]);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("imoNumber must be a valid long: " + parts[3], e);
        }
        if (imoNumber < 0) {
            throw new BadEncodingException("imoNumber must be at greater than or "
                + "equal to 0: " + parts[3]);
        }
        if (!Ship.shipExists(imoNumber)) {
            throw new BadEncodingException("Ship with the specified "
                + "imoNumber must exist: " + parts[3]);
        }
        Ship ship = null;
        try {
            ship = Ship.getShipByImoNumber(imoNumber);
        } catch (NoSuchShipException e) {
            // do nothing as we checked above
        }
        return new ShipMovement(time, direction, ship);
    }
}
