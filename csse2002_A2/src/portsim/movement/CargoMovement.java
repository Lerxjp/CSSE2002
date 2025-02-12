package portsim.movement;

import portsim.cargo.Cargo;
import portsim.util.BadEncodingException;
import portsim.util.NoSuchCargoException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The movement of cargo coming into or out of the port.
 *
 * @ass1_partial
 */
public class CargoMovement extends Movement {

    /**
     * The cargo that will be involved in the movement
     */
    private List<Cargo> cargo;

    /**
     * Creates a new cargo movement with the given action time and direction
     * to be undertaken with the given cargo.
     *
     * @param time      the time the movement should occur
     * @param direction the direction of the movement
     * @param cargo     the cargo to be moved
     * @throws IllegalArgumentException if time &lt; 0
     * @ass1
     */
    public CargoMovement(long time, MovementDirection direction,
                         List<Cargo> cargo) throws IllegalArgumentException {
        super(time, direction);
        this.cargo = cargo;
    }

    /**
     * Returns the cargo that will be moved.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     *
     * @return all cargo in the movement
     * @ass1
     */
    public List<Cargo> getCargo() {
        return new ArrayList<>(cargo);
    }

    /**
     * Returns the human-readable string representation of this CargoMovement.
     * <p>
     * The format of the string to return is
     * <pre>
     * DIRECTION CargoMovement to occur at time involving num piece(s) of cargo </pre>
     * Where:
     * <ul>
     *   <li>{@code DIRECTION} is the direction of the movement </li>
     *   <li>{@code time} is the time the movement is meant to occur </li>
     *   <li>{@code num} is the number of cargo pieces that are being moved</li>
     * </ul>
     * <p>
     * For example: <pre>
     * OUTBOUND CargoMovement to occur at 135 involving 5 piece(s) of cargo </pre>
     *
     * @return string representation of this CargoMovement
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s involving %d piece(s) of cargo",
            super.toString(),
            this.cargo.size());
    }

    /**
     * Returns the machine-readable string representation of this movement.
     * <p>
     * The format of the string to return is
     * <pre>CargoMovement:time:direction:numCargo:ID1,ID2,...</pre>
     * Where:
     *  <ul>
     *   <li>{@code time} is the time that the movement will be actioned</li>
     *   <li>{@code direction} is the direction of the movement</li>
     *   <li>{@code numCargo} is the number of the cargo in the movement</li>
     *   <li>{@code ID1,ID2,...} are the IDs of the cargo in the movement separated by a
     *   comma ','. There should be no trailing comma after the last ID.</li>
     * </ul>
     * For example:
     *
     * <pre>CargoMovement:120:INBOUND:3:22,23,12</pre>
     *
     * @return encoded string representation of this movement
     * @ass2
     */
    @Override
    public String encode() {
        return String.format("%s:%s:%s",
            super.encode(),
            cargo.size(),
            cargo.stream().map(Cargo::getId).map(Object::toString)
                .collect(Collectors.joining(",")));
    }

    /**
     * Creates a cargo movement from a string encoding.
     * <p>
     * The format of the string should match the encoded representation of a
     * cargo movement, as described in {@link CargoMovement#encode()}.
     * <p>
     * The encoded string is invalid if any of the following conditions are true:
     * <ul>
     * <li>The number of colons ({@code :}) detected was more/fewer than expected.</li>
     * <li>The given string is not a CargoMovement encoding</li>
     * <li>The time is not a long (i.e. cannot be parsed by
     * {@link Long#parseLong(String)}).</li>
     * <li>The time is less than zero (0).</li>
     * <li>The movementDirection is not one of the valid directions (See
     * {@link MovementDirection}).</li>
     * <li>The number of ids is not a int (i.e. cannot be parsed by
     * {@link Integer#parseInt(String)}).</li>
     * <li>The number of ids is less than one (1).</li>
     * <li>An id is not a int (i.e. cannot be parsed by
     * {@link Integer#parseInt(String)}).</li>
     * <li>An id is less than zero (0).</li>
     * <li>There is no cargo that exists with a specified id.</li>
     * <li>The number of id's does not match the number specified.</li>
     * </ul>
     *
     * @param string string containing the encoded CargoMovement
     * @return decoded CargoMovement instance
     * @throws BadEncodingException if the format of the given string is invalid according to
     *                              the rules above
     * @ass2
     */
    public static CargoMovement fromString(String string) throws BadEncodingException {

        String[] parts = string.split(":", -1);
        if (parts.length != 5) {
            throw new BadEncodingException("Invalid movement, was expecting 5"
                + " parts: " + string);
        }
        if (!parts[0].equals("CargoMovement")) {
            throw new BadEncodingException("Expected CargoMovement encoding: " + string);
        }
        // get the time the movement is actioned
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
        // get the direction of the movement
        MovementDirection direction;
        try {
            direction = MovementDirection.valueOf(parts[2]);
        } catch (IllegalArgumentException e) {
            throw new BadEncodingException("Invalid direction in encoding: " + parts[2], e);
        }
        // get the Cargo components
        int numCargo;
        // get the number of cargo
        try {
            numCargo = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("numParts must be a valid int: " + parts[3], e);
        }
        if (numCargo < 1) {
            throw new BadEncodingException("numCargo must be greater than or "
                + "equal to 1: " + parts[3]);
        }
        // get the cargo ids
        int id;
        List<Cargo> cargo = new ArrayList<>();
        String[] idParts = parts[4].split(",");
        if (numCargo != idParts.length) {
            throw new BadEncodingException("the number of Ids given must "
                + "match numCargo: " + parts[3] + "-> " + parts[4]);
        }
        for (String idComponent : idParts) {
            try {
                id = Integer.parseInt(idComponent);
            } catch (NumberFormatException e) {
                throw new BadEncodingException("id must be a valid int: " + idComponent, e);
            }
            if (!Cargo.cargoExists(id)) {
                throw new BadEncodingException("Cargo with the specified "
                    + "id must exist: " + idComponent);
            }
            try {
                cargo.add(Cargo.getCargoById(id));
            } catch (NoSuchCargoException ignored) {
                // do nothing as we checked above
            }
        }

        return new CargoMovement(time, direction, cargo);
    }
}
