package portsim.ship;

import portsim.cargo.Cargo;
import portsim.port.Quay;
import portsim.util.BadEncodingException;
import portsim.util.Encodable;
import portsim.util.NoSuchCargoException;
import portsim.util.NoSuchShipException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a ship whose movement is managed by the system.
 * <p>
 * Ships store various types of cargo which can be loaded and unloaded at a port.
 *
 * @ass1_partial
 */
public abstract class Ship implements Encodable {
    /**
     * Name of the ship
     */
    private String name;

    /**
     * Unique 7 digit identifier to identify this ship (no leading zero's [0])
     */
    private long imoNumber;

    /**
     * Port of origin of ship
     */
    private String originFlag;

    /**
     * Maritime flag designated for use on this ship
     */
    private NauticalFlag flag;

    /**
     * Database of all ships currently active in the simulation
     */
    private static Map<Long, Ship> shipRegistry = new HashMap<>();

    /**
     * Creates a new ship with the given
     * <a href="https://en.wikipedia.org/wiki/IMO_number">IMO number</a>,
     * name, origin port flag and nautical flag.
     * <p>
     * Finally, the ship should be added to the ship registry with the
     * IMO number as the key.
     *
     * @param imoNumber  unique identifier
     * @param name       name of the ship
     * @param originFlag port of origin
     * @param flag       the nautical flag this ship is flying
     * @throws IllegalArgumentException if a ship already exists with the given
     *                                  imoNumber, imoNumber &lt; 0 or imoNumber is not 7 digits
     *                                  long (no leading zero's [0])
     * @ass1_partial
     */
    public Ship(long imoNumber, String name, String originFlag,
                NauticalFlag flag) throws IllegalArgumentException {
        if (Ship.shipExists(imoNumber)) {
            throw new IllegalArgumentException("The specified ship already "
                + "exists (imoNumber not unique): " + imoNumber);
        }
        if (imoNumber < 0) {
            throw new IllegalArgumentException("The imoNumber of the ship "
                + "must be positive: " + imoNumber);
        }
        if (String.valueOf(imoNumber).length() != 7 || String.valueOf(imoNumber).startsWith("0")) {
            throw new IllegalArgumentException("The imoNumber of the ship "
                + "must have 7 digits (no leading zero's [0]): " + imoNumber);
        }
        this.imoNumber = imoNumber;
        this.name = name;
        this.originFlag = originFlag;
        this.flag = flag;
        shipRegistry.put(imoNumber, this);
    }

    /**
     * Checks if a ship exists in the simulation using its IMO number.
     *
     * @param imoNumber unique key to identify ship
     * @return true if there is a ship with key {@code imoNumber} else false
     * @ass2
     */
    public static boolean shipExists(long imoNumber) {
        return shipRegistry.containsKey(imoNumber);
    }

    /**
     * Returns the ship specified by the IMO number.
     *
     * @param imoNumber unique key to identify ship
     * @return Ship specified by the given IMO number
     * @throws NoSuchShipException if the ship does not exist
     * @ass2
     */
    public static Ship getShipByImoNumber(long imoNumber) throws NoSuchShipException {
        if (!shipExists(imoNumber)) {
            throw new NoSuchShipException("The ship with the specified "
                + "imoNUmber does not exist: " + imoNumber);
        }
        return shipRegistry.get(imoNumber);
    }

    /**
     * Check if this ship can dock with the specified quay according
     * to the conditions determined by the ships type.
     *
     * @param quay quay to be checked
     * @return true if the Quay satisfies the conditions else false
     * @ass1
     */
    public abstract boolean canDock(Quay quay);

    /**
     * Checks if the specified cargo can be loaded onto the ship according
     * to the conditions determined by the ships type and contents.
     *
     * @param cargo cargo to be loaded
     * @return true if the Cargo satisfies the conditions else false
     * @ass1
     */
    public abstract boolean canLoad(Cargo cargo);

    /**
     * Loads the specified cargo onto the ship.
     *
     * @param cargo cargo to be loaded
     * @require Cargo given is able to be loaded onto this ship according to
     * the implementation of {@link Ship#canLoad(Cargo)}
     * @ass1
     */
    public abstract void loadCargo(Cargo cargo);

    /**
     * Returns this ship's name.
     *
     * @return name
     * @ass1
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns this ship's IMO number.
     *
     * @return imoNumber
     * @ass1
     */
    public long getImoNumber() {
        return this.imoNumber;
    }

    /**
     * Returns this ship's flag denoting its origin.
     *
     * @return originFlag
     * @ass1
     */
    public String getOriginFlag() {
        return this.originFlag;
    }

    /**
     * Returns the nautical flag the ship is flying.
     *
     * @return flag
     * @ass1
     */
    public NauticalFlag getFlag() {
        return this.flag;
    }

    /**
     * Returns the database of ships currently active in the simulation as a mapping from
     * the ship's IMO number to its Ship instance.
     * <p>
     * Adding or removing elements from the returned map should not affect the
     * original map.
     *
     * @return ship registry database
     * @ass2
     */
    public static Map<Long, Ship> getShipRegistry() {
        return new HashMap<>(shipRegistry);
    }

    /**
     * Returns true if and only if this ship is equal to the other given ship.
     * <p>
     * For two ships to be equal, they must have the same name, flag, origin
     * port, and IMO number.
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
        if (!(o instanceof Ship)) {
            return false;
        }

        Ship other = (Ship) o;

        if (!other.name.equals(this.name) || !other.flag.equals(this.flag)
            || !other.originFlag.equals(this.originFlag)) {
            return false;
        }

        return other.imoNumber == this.imoNumber;
    }

    /**
     * Returns the hash code of this ship.
     * <p>
     * Two ships that are equal according to the {@link #equals(Object)} method should have the same
     * hash code.
     *
     * @return hash code of this ship.
     * @ass2
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.imoNumber, this.flag,
            this.originFlag);
    }

    /**
     * Returns the human-readable string representation of this Ship.
     * <p>
     * The format of the string to return is
     * <pre>ShipClass name from origin [flag]</pre>
     * Where:
     * <ul>
     *   <li>{@code ShipClass} is the Ship class</li>
     *   <li>{@code name} is the name of this ship</li>
     *   <li>{@code origin} is the country of origin of this ship</li>
     *   <li>{@code flag} is the nautical flag of this ship</li>
     * </ul>
     * For example: <pre>BulkCarrier Evergreen from Australia [BRAVO]</pre>
     *
     * @return string representation of this Ship
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s %s from %s [%s]",
            this.getClass().getSimpleName(),
            this.name,
            this.originFlag,
            this.flag);
    }

    /**
     * Returns the machine-readable string representation of this Ship.
     * <p>
     * The format of the string to return is
     * <pre>ShipClass:imoNumber:name:origin:flag</pre>
     * Where:
     * <ul>
     *   <li>{@code ShipClass} is the Ship class name</li>
     *   <li>{@code imoNumber} is the IMO number of the ship</li>
     *   <li>{@code name} is the name of this ship </li>
     *   <li>{@code origin} is the country of origin of this ship </li>
     *   <li>{@code flag} is the nautical flag of this ship </li>
     * </ul>
     * For example:
     *
     * <pre>Ship:1258691:Evergreen:Australia:BRAVO</pre>
     *
     * @return encoded string representation of this Ship
     * @ass2
     */
    @Override
    public String encode() {
        return String.format("%s:%d:%s:%s:%s",
            this.getClass().getSimpleName(),
            this.imoNumber,
            this.name,
            this.originFlag,
            this.flag);
    }

    /**
     * Reads a Ship from its encoded representation in the given string.
     * <p>
     * The format of the string should match the encoded representation of a
     * Ship, as described in {@link Ship#encode()} (and subclasses).
     * <p>
     * The encoded string is invalid if any of the following conditions are true:
     * <ul>
     * <li>The number of colons ({@code :}) detected was more/fewer than expected</li>
     * <li>The ship's IMO number is not a long (i.e. cannot be parsed by
     * {@link Long#parseLong(String)})</li>
     * <li>The ship's IMO number is valid according to the constructor  </li>
     * <li>The ship's type specified is not one of {@link ContainerShip} or
     * {@link BulkCarrier}</li>
     * <li>The encoded Nautical flag is not one of {@link NauticalFlag#values()}</li>
     * <li>The encoded cargo to add does not exist in the simulation according to
     * {@link Cargo#cargoExists(int)}</li>
     * <li>The encoded cargo can not be added to the ship according to
     * {@link Ship#canLoad(Cargo)}<br><b>NOTE: Keep this in mind when making your own save
     * files</b></li>
     * <li>Any of the parsed values given to a subclass constructor causes an
     * {@link IllegalArgumentException}.</li>
     * </ul>
     *
     * @param string string containing the encoded Ship
     * @return decoded ship instance
     * @throws BadEncodingException if the format of the given string is
     *                              invalid according to the rules above
     * @ass2
     */
    public static Ship fromString(String string) throws BadEncodingException {
        String[] encodedShip = string.split(":", -1);
        if (encodedShip.length < 6 || encodedShip.length > 8) {
            throw new BadEncodingException("Invalid ship line length");
        }

        long imoNumber;
        try {
            imoNumber = Long.parseLong(encodedShip[1]);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("IMO number must be a long: " + encodedShip[1], e);
        }

        String shipType = encodedShip[0];
        Ship ship = null;
        if (shipType.equals("ContainerShip")) {
            if (encodedShip.length != 8 && encodedShip.length != 7) {
                throw new BadEncodingException("The encoded ContainerShip "
                    + "line is of incorrect length: " + string);
            }
        } else if (shipType.equals("BulkCarrier")) {
            if (encodedShip.length != 7 && encodedShip.length != 6) {
                throw new BadEncodingException("The encoded BulkCarrier "
                    + "line is of incorrect length: " + string);
            }
        }

        NauticalFlag flag;
        try {
            flag = NauticalFlag.valueOf(encodedShip[4]);
        } catch (IllegalArgumentException e) {
            throw new BadEncodingException("Invalid nautical flag type: " + string);
        }

        int capacity;
        try {
            capacity = Integer.parseInt(encodedShip[5]);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("Capacity must be"
                + " integer: " + encodedShip[5], e);
        }

        if (shipType.equals("ContainerShip")) {
            int containerContents;
            try {
                containerContents = Integer.parseInt(encodedShip[6]);
            } catch (NumberFormatException e) {
                throw new BadEncodingException("The number of Containers on board must "
                    + "be an integer");
            }
            try {
                ship = new ContainerShip(imoNumber, encodedShip[2],
                    encodedShip[3], flag, capacity);
            } catch (IllegalArgumentException e) {
                // checks for valid constructor arguements
                throw new BadEncodingException(e);
            }

            if (encodedShip.length == 8 && !encodedShip[7].equals("")) {
                String[] cargo = encodedShip[7].split(",");
                int cargoId;
                Cargo cargoToAdd;
                if (containerContents != 0) {
                    if (cargo.length != containerContents) {
                        throw new BadEncodingException("Number of cargo onboard "
                            + "must match previously specified size of list " + containerContents);
                    }
                    for (int i = 0; i < cargo.length; i++) {
                        try {
                            cargoId = Integer.parseInt(cargo[i]);
                        } catch (NumberFormatException e) {
                            throw new BadEncodingException("The container ship had "
                                + "invalid cargo onboard: " + cargo[i]);
                        }
                        try {
                            cargoToAdd = Cargo.getCargoById(cargoId);
                        } catch (NoSuchCargoException e) {
                            throw new BadEncodingException("The specified container "
                                + "on ship does not exist in system: " + cargo[i]);
                        }
                        if (!ship.canLoad(cargoToAdd)) {
                            throw new BadEncodingException("Specified cargo can "
                                + "not be added to this ship " + cargoToAdd.toString());
                        }
                        ship.loadCargo(cargoToAdd);
                    }
                }
            }

        } else if (shipType.equals("BulkCarrier")) {
            int id;
            Cargo cargoToAdd = null;
            if (encodedShip.length == 7 && !encodedShip[6].equals("")) {
                try {
                    id = Integer.parseInt(encodedShip[6]);
                } catch (NumberFormatException e) {
                    throw new BadEncodingException("Cargo ID must be integer");
                }

                try {
                    cargoToAdd = Cargo.getCargoById(id);
                } catch (NoSuchCargoException e) {
                    throw new BadEncodingException("The specified cargo on ship "
                        + "does not exist in system: " + id);
                }
            }

            ship = new BulkCarrier(imoNumber, encodedShip[2],
                encodedShip[3], flag, capacity);

            if (cargoToAdd != null) {
                if (!ship.canLoad(cargoToAdd)) {
                    throw new BadEncodingException("Specified cargo can "
                        + "not be added to this ship");
                }
                ship.loadCargo(cargoToAdd);
            }


        } else {
            throw new BadEncodingException("Invalid ship type: " + shipType);
        }
        return ship;
    }

    /**
     * Resets the global ship registry.
     * This utility method is for the testing suite.
     *
     * @given
     */
    public static void resetShipRegistry() {
        Ship.shipRegistry = new HashMap<>();
    }
}
