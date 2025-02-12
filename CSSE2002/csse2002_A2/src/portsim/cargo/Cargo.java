package portsim.cargo;

import portsim.util.BadEncodingException;
import portsim.util.Encodable;
import portsim.util.NoSuchCargoException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Denotes a cargo whose function is to be transported via a Ship or land
 * transport.
 * <p>
 * Cargo is kept track of via its ID.
 *
 * @ass1_partial
 */
public abstract class Cargo implements Encodable {
    /**
     * The ID of the cargo instance
     */
    private int id;

    /**
     * Destination for this cargo
     */
    private String destination;

    /**
     * Database of all cargo currently active in the simulation
     */
    private static Map<Integer, Cargo> cargoRegistry = new HashMap<>();

    /**
     * Creates a new Cargo with the given ID and destination port.
     * <p>
     * When a new piece of cargo is created, it should be added to the cargo registry.
     * @param id          cargo ID
     * @param destination destination port
     * @throws IllegalArgumentException if a cargo already exists with the
     *                                  given ID or ID &lt; 0
     * @ass1_partial
     */
    public Cargo(int id, String destination) throws IllegalArgumentException {
        if (Cargo.cargoExists(id)) {
            throw new IllegalArgumentException("The specified cargo already "
                + "exists: " + id);
        }
        if (id < 0) {
            throw new IllegalArgumentException("Cargo ID must be greater than"
                + " or equal to 0: " + id);
        }
        this.id = id;
        this.destination = destination;
        cargoRegistry.put(id, this);
    }

    /**
     * Retrieve the ID of this piece of cargo.
     *
     * @return the cargo's ID
     * @ass1
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieve the destination of this piece of cargo.
     *
     * @return the cargo's destination
     * @ass1
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Returns the global registry of all pieces of cargo, as a mapping
     * from cargo IDs to Cargo instances.
     * <p>
     * Adding or removing elements from the returned map should not
     * affect the original map.
     *
     * @return cargo registry
     * @ass2
     */
    public static Map<Integer, Cargo> getCargoRegistry() {
        return new HashMap<>(cargoRegistry);
    }

    /**
     * Checks if a cargo exists in the simulation using its ID.
     *
     * @param id unique key to identify cargo
     * @return true if there is a cargo stored in the registry with key
     * {@code id}; false otherwise
     * @ass2
     */
    public static boolean cargoExists(int id) {
        return cargoRegistry.containsKey(id);
    }

    /**
     * Returns the cargo specified by the given ID.
     *
     * @param id unique key to identify cargo
     * @return cargo specified by the id
     * @throws NoSuchCargoException if the cargo does not exist in the registry
     * @ass2
     */
    public static Cargo getCargoById(int id) throws NoSuchCargoException {
        if (!cargoExists(id)) {
            throw new NoSuchCargoException("The cargo with the specified id does not exist");
        }
        return cargoRegistry.get(id);

    }

    /**
     * Returns true if and only if this cargo is equal to the other given cargo.
     * <p>
     * For two cargo to be equal, they must have the same ID and destination.
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
        if (!(o instanceof Cargo)) {
            return false;
        }

        Cargo other = (Cargo) o;

        if (!other.destination.equals(this.destination)) {
            return false;
        }

        return other.id == this.id;
    }

    /**
     * Returns the hash code of this cargo.
     * <p>
     * Two cargo are equal according to {@link Cargo#equals(Object)} method should have the same
     * hash code.
     *
     * @return hash code of this cargo.
     * @ass2
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.destination);
    }

    /**
     * Returns the human-readable string representation of this cargo.
     * <p>
     * The format of the string to return is
     * <pre>CargoClass id to destination</pre>
     * Where:
     * <ul>
     *   <li>{@code CargoClass} is the cargo class name</li>
     *   <li>{@code id} is the id of this cargo </li>
     *   <li>{@code destination} is the destination of the cargo </li>
     * </ul>
     * <p>
     * For example: <pre>Container 55 to New Zealand</pre>
     *
     * @return string representation of this Cargo
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s %d to %s",
            this.getClass().getSimpleName(),
            this.id,
            this.destination);
    }

    /**
     * Returns the machine-readable string representation of this Cargo.
     * <p>
     * The format of the string to return is
     * <pre>CargoClass:id:destination</pre>
     * Where:
     * <ul>
     *   <li>{@code CargoClass} is the Cargo class name</li>
     *   <li>{@code id} is the id of this cargo </li>
     *   <li>{@code destination} is the destination of this cargo </li>
     * </ul>
     * <p>
     * For example:
     *
     * <pre>Container:3:Australia</pre> OR <pre>BulkCargo:2:France</pre>
     *
     * @return encoded string representation of this Cargo
     * @ass2
     */
    @Override
    public String encode() {
        return String.format("%s:%d:%s",
            this.getClass().getSimpleName(),
            this.id,
            this.destination);
    }

    /**
     * Reads a piece of cargo from its encoded representation in the given
     * string.
     * <p>
     * The format of the given string should match the encoded representation of a
     * Cargo, as described in {@link Cargo#encode()} (and subclasses).
     * <p>
     * The encoded string is invalid if any of the following conditions are true:
     * <ul>
     * <li>The number of colons ({@code :}) detected was more/fewer than expected.</li>
     * <li>The cargo id is not an integer (i.e. cannot be parsed by
     * {@link Integer#parseInt(String)}).</li>
     * <li>The cargo id is less than zero (0).</li>
     * <li>A piece of cargo with the specified ID already exists</li>
     * <li>The cargo type specified is not one of {@link BulkCargoType} or
     * {@link ContainerType}</li>
     * <li>If the cargo type is a BulkCargo:
     *     <ol>
     *     <li>The cargo weight in tonnes is not an integer (i.e. cannot be parsed by
     *      {@link Integer#parseInt(String)}).</li>
     *     <li>The cargo weight in tonnes is less than zero (0).</li>
     *     </ol>
     * </li>
     * </ul>
     *
     * @param string string containing the encoded cargo
     * @return decoded cargo instance
     * @throws BadEncodingException if the format of the given string is
     *                              invalid according to the rules above
     * @ass2
     */
    public static Cargo fromString(String string) throws BadEncodingException {

        String[] encodedCargo = string.split(":", -1);

        if (encodedCargo.length != 4 && encodedCargo.length != 5) {
            throw new BadEncodingException("Encoded cargo should contain 4 or 5 \":\"");
        }
        // Cargo ID
        int id;
        try {
            id = Integer.parseInt(encodedCargo[1]);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("Id number must be an integer: " + encodedCargo[1],
                e);
        }
        String country = encodedCargo[2];

        if (encodedCargo.length == 4 && encodedCargo[0].equals("Container")) {
            // Container instance
            ContainerType type = null;
            try {
                type = ContainerType.valueOf(encodedCargo[3]);
            } catch (IllegalArgumentException e) {
                throw new BadEncodingException("Invalid container type: " + string);
            }
            try {
                return new Container(id, country, type);
            } catch (IllegalArgumentException e) {
                // This checks for Valid ID and Negative values
                throw new BadEncodingException(e);
            }
        } else if (encodedCargo[0].equals("BulkCargo")) {
            // implicit length of 5
            // BulkCargo instance
            BulkCargoType type = null;
            try {
                type = BulkCargoType.valueOf(encodedCargo[3]);
            } catch (IllegalArgumentException e) {
                throw new BadEncodingException("Invalid BulkCargo type: " + string);
            }
            int tonnage = 0;
            try {
                tonnage = Integer.parseInt(encodedCargo[4]);
            } catch (NumberFormatException e) {
                throw new BadEncodingException("Bulk cargo tonnage amount must "
                    + "be an integer", e);
            }
            try {
                return new BulkCargo(id, country, tonnage, type);
            } catch (IllegalArgumentException e) {
                // This checks for Valid ID and Negative values
                throw new BadEncodingException(e);
            }
        } else {
            throw new BadEncodingException(
                "Cargo encodings should have a  valid type: " + encodedCargo[0]);
        }
    }

    /**
     * Resets the global cargo registry.
     * This utility method is for the testing suite.
     *
     * @given
     */
    public static void resetCargoRegistry() {
        Cargo.cargoRegistry = new HashMap<>();
    }
}
