package portsim.port;

import portsim.ship.ContainerShip;
import portsim.ship.NauticalFlag;
import portsim.ship.Ship;
import portsim.util.BadEncodingException;
import portsim.util.Encodable;
import portsim.util.NoSuchShipException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Queue of ships waiting to enter a Quay at the port. Ships are chosen based
 * on their priority.
 *
 * @ass2
 */
public class ShipQueue implements Encodable {

    /**
     * List of ships, stored in insertion order
     */
    private List<Ship> ships;

    /**
     * Constructs a new ShipQueue with an initially empty queue of ships.
     *
     * @ass2
     */
    public ShipQueue() {
        this.ships = new LinkedList<>();
    }

    /**
     * Gets the next ship to enter the port and removes it from the queue.
     * <p>
     * The same rules as described in {@link ShipQueue#peek()} should be used for determining
     * which ship to remove and return.
     *
     * @return next ship to dock
     * @ass2
     */
    public Ship poll() {
        Ship peeked = peek();
        this.ships.remove(peeked);
        return peeked;
    }

    /**
     * Returns the next ship waiting to enter the port.
     * The queue should not change.
     * <p>
     * The rules for determining which ship in the queue should be returned
     * next are as follows:
     * <ol>
     *     <li> If a ship is carrying dangerous cargo, it should be
     *     returned. If more than one ship is carrying dangerous cargo
     *     return the one added to the queue first. </li>
     *     <li> If a ship requires medical assistance, it should be
     *     returned. If more than one ship requires medical assistance,
     *     return the one added to the queue first. </li>
     *     <li> If a ship is ready to be docked, it should be returned. If
     *     more than one ship is ready to be docked, return the one added
     *     to the queue first. </li>
     *     <li> If there is a container ship in the queue, return the one
     *     added to the queue first. </li>
     *     <li> If this point is reached and no ship has been returned,
     *     return the ship that was added to the queue first.</li>
     *     <li> If there are no ships in the queue, return null.</li>
     * </ol>
     *
     * @return next ship in queue
     * @ass2
     */
    public Ship peek() {
        // Ships carrying dangerous cargo are first priority
        for (Ship s : ships) {
            if (s.getFlag().equals(NauticalFlag.BRAVO)) {
                return s;
            }
        }
        for (Ship s : ships) {
            if (s.getFlag().equals(NauticalFlag.WHISKEY)) {
                return s;
            }
        }
        for (Ship s : ships) {
            if (s.getFlag().equals(NauticalFlag.HOTEL)) {
                return s;
            }
        }
        for (Ship s : ships) {
            if (s instanceof ContainerShip) {
                return s;
            }
        }

        return ships.size() > 0 ? ships.get(0) : null;
    }

    /**
     * Adds the specified ship to the queue.
     *
     * @param ship to be added to queue
     * @ass2
     */
    public void add(Ship ship) {
        this.ships.add(ship);
    }

    /**
     * Returns a list containing all the ships currently stored in this ShipQueue.
     * <p>
     * The order of the ships in the returned list should be the order in which the ships
     * were added to the queue.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     *
     * @return ships in queue
     * @ass2
     */
    public List<Ship> getShipQueue() {
        return new ArrayList<>(this.ships);
    }

    /**
     * Returns true if and only if this ship queue is equal to the other given ship queue.
     * <p>
     * For two ship queue to be equal, they must have the same ships in the queue, in the same
     * order.
     *
     * @param o other object to check equality
     * @return true if equal, false otherwise
     * @ass2
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShipQueue)) {
            return false;
        }

        ShipQueue other = (ShipQueue) o;

        return other.ships.equals(this.ships);
    }

    /**
     * Returns the hash code of this ship queue.
     * <p>
     * Two ship queue's that are equal according to {@link ShipQueue#equals(Object)} method
     * should have the same hash code.
     *
     * @return hash code of this ship queue.
     * @ass2
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.ships);
    }

    /**
     * Returns the machine-readable string representation of this ShipQueue.
     * <p>
     * The format of the string to return is
     * <pre>ShipQueue:numShipsInQueue:shipID,shipID,...</pre>
     * Where:
     * <ul>
     *   <li>numShipsInQueue is the total number of ships in the ship queue
     *   in the port</li>
     *   <li>If present (numShipsInQueue &gt; 0): shipID is each ship's ID in the aforementioned
     *   queue</li>
     * </ul>
     * For example:
     *
     * <pre>ShipQueue:0:</pre> or <pre>ShipQueue:2:3456789,1234567</pre>
     *
     * @return encoded string representation of this ShipQueue
     * @ass2
     */
    @Override
    public String encode() {
        return String.format("%s:%d:%s",
            this.getClass().getSimpleName(),
            this.ships.size(),
            this.ships.stream().map(Ship::getImoNumber).map(Object::toString)
                .collect(Collectors.joining(",")));
    }

    /**
     * Creates a ship queue from a string encoding.
     * <p>
     * The format of the string should match the encoded representation of a
     * ship queue, as described in {@link ShipQueue#encode()}.
     * <p>
     * The encoded string is invalid if any of the following conditions are true:
     * <ul>
     * <li>The number of colons ({@code :}) detected was more/fewer than expected.</li>
     * <li>The string does not start with the literal string {@code "ShipQueue"}</li>
     * <li>The number of ships in the shipQueue is not an integer (i.e. cannot
     * be parsed by {@link Integer#parseInt(String)}).</li>
     * <li>The number of ships in the shipQueue does not match the number specified.</li>
     * <li>The imoNumber of the ships in the shipQueue are not valid longs.
     * (i.e. cannot be parsed by {@link Long#parseLong(String)}).</li>
     * <li>Any imoNumber read does not correspond to a valid ship in the
     * simulation</li>
     * </ul>
     *
     * @param string string containing the encoded ShipQueue
     * @return decoded ship queue instance
     * @throws BadEncodingException if the format of the given string is invalid according to
     *                              the rules above
     * @ass2
     */
    public static ShipQueue fromString(String string) throws BadEncodingException {
        String[] shipQueueEncoded = string.split(":", -1);
        if (!(shipQueueEncoded[0].equals("ShipQueue")) || shipQueueEncoded.length != 3) {
            throw new BadEncodingException("Expected encoded ship queue here:"
                + " " + shipQueueEncoded[0]);
        }
        int numShipsInQueue;
        try {
            numShipsInQueue = Integer.parseInt(shipQueueEncoded[1]);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("The number of ships in the ship "
                + "queue should be an integer");
        }
        String[] shipsInQueue = shipQueueEncoded[2].split(",");
        List<Long> decodedShips = new ArrayList<>();
        if (numShipsInQueue != shipsInQueue.length && numShipsInQueue > 0 || numShipsInQueue < 0) {
            throw new BadEncodingException("The number of ships in the ship "
                + "queue should be a equal to the number encoded");
        }
        try {
            for (int i = 0; i < shipsInQueue.length && numShipsInQueue > 0; i++) {
                decodedShips.add(Long.parseLong(shipsInQueue[i]));
            }
        } catch (NumberFormatException e) {
            throw new BadEncodingException("Ships in queue should be "
                + "represented by their imo number");
        }

        // check all ships in queue exist
        for (Long shipId : decodedShips) {
            if (!Ship.shipExists(shipId)) {
                throw new BadEncodingException("This ship in the queue does "
                    + "not exist in this system: " + shipId);
            }
        }

        //Create a shipqueue
        ShipQueue queue = new ShipQueue();
        for (Long ships : decodedShips) {
            try {
                queue.add(Ship.getShipByImoNumber(ships));
            } catch (NoSuchShipException e) {
                //unreachable
            }

        }
        return queue;
    }
}
