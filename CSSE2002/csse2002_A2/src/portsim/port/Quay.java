package portsim.port;

import portsim.ship.Ship;
import portsim.util.BadEncodingException;
import portsim.util.Encodable;
import portsim.util.NoSuchShipException;

import java.util.Objects;

/**
 * Quay is a platform lying alongside or projecting into the water where
 * ships are moored for loading or unloading.
 *
 * @ass1_partial
 */
public abstract class Quay implements Encodable {
    /**
     * The ID of the quay
     */
    private int id;

    /**
     * The ship currently in the Quay
     */
    private Ship ship;

    /**
     * Creates a new Quay with the given ID, with no ship docked at the quay.
     *
     * @param id quay ID
     * @throws IllegalArgumentException if ID &lt; 0
     * @ass1
     */
    public Quay(int id) throws IllegalArgumentException {
        if (id < 0) {
            throw new IllegalArgumentException("Quay ID must be greater than"
                + " or equal to 0: " + id);
        }
        this.id = id;
        this.ship = null;
    }

    /**
     * Get the id of this quay
     *
     * @return quay id
     * @ass1
     */
    public int getId() {
        return id;
    }

    /**
     * Docks the given ship at the Quay so that the quay becomes occupied.
     *
     * @param ship ship to dock to the quay
     * @ass1
     */
    public void shipArrives(Ship ship) {
        this.ship = ship;
    }

    /**
     * Removes the current ship docked at the quay.
     * The current ship should be set to {@code null}.
     *
     * @return the current ship or null if quay is empty.
     * @ass1
     */
    public Ship shipDeparts() {
        Ship current = this.ship;
        this.ship = null;
        return current;
    }

    /**
     * Returns whether a ship is currently docked at this quay.
     *
     * @return true if there is no ship docked else false
     * @ass1
     */
    public boolean isEmpty() {
        return this.ship == null;
    }

    /**
     * Returns the ship currently docked at the quay.
     *
     * @return ship at quay or null if no ship is docked
     * @ass1
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Returns true if and only if this Quay is equal to the other given Quay.
     * <p>
     * For two Quays to be equal, they must have the same ID and
     * ship docked status (must either both be empty or both be occupied).
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
        if (!(o instanceof Quay)) {
            return false;
        }

        Quay other = (Quay) o;

        if (this.isEmpty() != other.isEmpty()) {
            return false;
        }

        return other.id == this.id;
    }

    /**
     * Returns the hash code of this quay.
     * <p>
     * Two quays that are equal according to {@link Quay#equals(Object)} method
     * should have the same hash code.
     *
     * @return hash code of this quay.
     * @ass2
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.isEmpty());
    }

    /**
     * Returns the human-readable string representation of this quay.
     * <p>
     * The format of the string to return is
     * <pre>QuayClass id [Ship: imoNumber]</pre>
     * Where:
     * <ul>
     * <li>{@code id} is the ID of this quay</li>
     * <li>{@code imoNumber} is the IMO number of the ship docked at this
     * quay, or {@code None} if the quay is unoccupied.</li>
     * </ul>
     * <p>
     * For example: <pre>BulkQuay 1 [Ship: 2313212]</pre> or
     * <pre>ContainerQuay 3 [Ship: None]</pre>
     *
     * @return string representation of this quay
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s %d [Ship: %s]",
            this.getClass().getSimpleName(),
            this.id,
            (this.ship != null ? this.ship.getImoNumber() : "None"));
    }

    /**
     * Returns the machine-readable string representation of this Quay.
     * <p>
     * The format of the string to return is
     * <pre>QuayClass:id:imoNumber</pre>
     * Where:
     * <ul>
     *   <li>{@code QuayClass} is the Quay class name</li>
     *   <li>{@code id} is the ID of this quay </li>
     *   <li>{@code imoNumber} is the IMO number of the ship docked at this
     *   quay, or {@code None} if the quay is unoccupied.</li>
     * </ul>
     * For example:
     *
     * <pre>BulkQuay:3:1258691</pre> or <pre>ContainerQuay:3:None</pre>
     *
     * @return encoded string representation of this quay
     * @ass2
     */
    @Override
    public String encode() {
        return String.format("%s:%d:%s",
            this.getClass().getSimpleName(),
            this.id,
            (this.ship != null ? this.ship.getImoNumber() : "None"));
    }

    /**
     * Reads a Quay from its encoded representation in the given
     * string.
     * <p>
     * The format of the string should match the encoded representation of a
     * Quay, as described in {@link Quay#encode()} (and subclasses).
     * <p>
     * The encoded string is invalid if any of the following conditions are true:
     * <ul>
     * <li>The number of colons ({@code :}) detected was more/fewer than expected.</li>
     * <li>The quay id is not an Integer (i.e. cannot be parsed by
     * {@link Integer#parseInt(String)}).</li>
     * <li>The quay id is less than 0 (0).</li>
     * <li>The quay type specified is not one of {@link BulkQuay} or
     * {@link ContainerQuay}</li>
     * <li>If the encoded ship is not {@code None} then the ship must exist
     * and the imoNumber specified must be a long (i.e. can be parsed by
     * {@link Long#parseLong(String)}).
     * </li>
     * <li>The quay capacity is not an integer (i.e. cannot be parsed by
     * {@link Integer#parseInt(String)}).</li>
     * <li>Any of the parsed values given to a subclass constructor causes an
     * {@link IllegalArgumentException}.</li>
     * </ul>
     *
     * @param string string containing the encoded Quay
     * @return decoded Quay instance
     * @throws BadEncodingException if the format of the given string is
     *                              invalid according to the rules above
     * @ass2
     */
    public static Quay fromString(String string) throws BadEncodingException {
        String[] encodedQuay = string.split(":");
        Quay quay;
        if (encodedQuay.length != 4) {
            throw new BadEncodingException("Encoded quay is not of "
                + "correct length");
        }

        int id;
        try {
            id = Integer.parseInt(encodedQuay[1]);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("The quay's id must be an "
                + "integer");
        }
        int capacity;
        Ship toAdd = null;
        long imoNumber;
        if (!encodedQuay[2].equals("None")) {
            try {
                imoNumber = Long.parseLong(encodedQuay[2]);
            } catch (NumberFormatException e) {
                throw new BadEncodingException("The imo number of the ship "
                    + "docked at the quay must be an integer");
            }

            try {
                toAdd = Ship.getShipByImoNumber(imoNumber);
            } catch (NoSuchShipException e) {
                throw new BadEncodingException("The specified ship for "
                    + "this quay does not exist");
            }
        }
        try {
            capacity = Integer.parseInt(encodedQuay[3]);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("The quay's capacity/tonnage "
                + "is not an integer");
        }

        if (encodedQuay[0].equals("ContainerQuay")) {
            try {
                quay = new ContainerQuay(id, capacity);
            } catch (IllegalArgumentException e) {
                throw new BadEncodingException(e);
            }

        } else if (encodedQuay[0].equals("BulkQuay")) {
            try {
                quay = new BulkQuay(id, capacity);
            } catch (IllegalArgumentException e) {
                throw new BadEncodingException(e);
            }
        } else {
            throw new BadEncodingException("The encoded quay uses an invalid "
                + "quay type: " + encodedQuay[0]);
        }
        // if toAdd is null there is no change.
        quay.shipArrives(toAdd);
        return quay;
    }

}
