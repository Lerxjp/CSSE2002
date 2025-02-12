package portsim.port;

import portsim.cargo.Cargo;
import portsim.evaluators.*;
import portsim.movement.CargoMovement;
import portsim.movement.Movement;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;
import portsim.ship.BulkCarrier;
import portsim.ship.ContainerShip;
import portsim.ship.Ship;
import portsim.util.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * A place where ships can come and dock with Quays to load / unload their
 * cargo.
 * <p>
 * Ships can enter a port through its queue. Cargo is stored within the port at warehouses.
 *
 * @ass1_partial
 */
public class Port implements Tickable, Encodable {

    /**
     * The name of this port used for identification
     */
    private String name;
    /**
     * The current time elapsed since the simulation started
     */
    private long time;
    /**
     * The quays associated with this port
     */
    private List<Quay> quays;
    /**
     * The queue of ships who want to interact with the port
     */
    private ShipQueue shipQueue;
    /**
     * The cargo currently stored at the port at warehouses. Cargo unloaded from trucks / ships
     */
    private List<Cargo> storedCargo;
    /**
     * The movements that cargo and ships will undergo at this port
     */
    private PriorityQueue<Movement> movements;
    /**
     * The statistics evaluators associated with the port
     */
    private List<StatisticsEvaluator> evaluators;

    /**
     * Creates a new port with the given name.
     * <p>
     * The time since the simulation was started should be initialised as 0.
     * <p>
     * The list of quays in the port, stored cargo (warehouses) and statistics evaluators should be
     * initialised as empty lists.
     * <p>
     * An empty ShipQueue should be initialised, and a PriorityQueue should be initialised
     * to store movements ordered by the time of the movement (see {@link Movement#getTime()}).
     *
     * @param name name of the port
     * @ass1_partial
     */
    public Port(String name) {
        this.name = name;
        this.time = 0;
        this.quays = new ArrayList<Quay>();
        this.shipQueue = new ShipQueue();
        this.storedCargo = new ArrayList<Cargo>();
        this.movements =
            new PriorityQueue<Movement>(Comparator.comparingLong(Movement::getTime));
        this.evaluators = new ArrayList<StatisticsEvaluator>();
    }

    /**
     * Creates a new port with the given name, time elapsed, ship queue, quays and stored cargo.
     * <p>
     * The list of statistics evaluators should be initialised as an empty list.
     * <p>
     * An empty ShipQueue should be initialised, and a PriorityQueue should be initialised
     * to store movements ordered by the time of the movement (see {@link Movement#getTime()}).
     *
     * @param name        name of the port
     * @param time        number of minutes since simulation started
     * @param shipQueue   ships waiting to enter the port
     * @param quays       the port's quays
     * @param storedCargo the cargo stored at the port
     * @throws IllegalArgumentException if time &lt; 0
     * @ass2
     */
    public Port(String name, long time, ShipQueue shipQueue, List<Quay> quays,
                List<Cargo> storedCargo) throws IllegalArgumentException {
        if (time < 0) {
            throw new IllegalArgumentException("Time should be greater than "
                + "or equal to 0");
        }
        this.name = name;
        this.time = time;
        this.quays = quays;
        this.shipQueue = shipQueue;
        this.storedCargo = storedCargo;
        this.movements =
            new PriorityQueue<Movement>(Comparator.comparingLong(Movement::getTime));
        this.evaluators = new ArrayList<StatisticsEvaluator>();
    }

    /**
     * Adds a movement to the PriorityQueue of movements.
     * <p>
     * If the given movement's action time is less than the current number of
     * minutes elapsed than an {@code IllegalArgumentException} should be
     * thrown.
     *
     * @param movement movement to add
     * @ass2
     */
    public void addMovement(Movement movement) throws IllegalArgumentException {
        if (movement.getTime() < time) {
            throw new IllegalArgumentException("Movements can not be set for "
                + "a time in the past. Current time: " + time);
        }
        this.movements.add(movement);
    }

    /**
     * Processes a movement.
     * <p>
     * The action taken depends on the type of movement to be processed.
     * <p>
     * If the movement is a ShipMovement:
     * <ul>
     * <li>If the movement direction is {@code INBOUND} then the ship should be added to
     * the ship queue.</li>
     * <li>If the movement direction is {@code OUTBOUND} then any cargo stored in the port whose
     * destination is the ship's origin port should be added to the ship according to
     * {@link Ship#canLoad(Cargo)}. Next, the ship should be removed from the quay it is
     * currently docked in (if any).</li>
     * </ul>
     * If the movement is a CargoMovement:
     * <ul>
     * <li>If the movement direction is {@code INBOUND} then all of the cargo that is
     * being moved should be added to the port's stored cargo.</li>
     * <li>If the movement direction is {@code OUTBOUND} then all cargo with the given
     * IDs should be removed from the port's stored cargo.</li>
     * </ul>
     * <p>
     * Finally, the movement should be forwarded onto each statistics evaluator stored by the port
     * by calling {@link StatisticsEvaluator#onProcessMovement(Movement)}.
     *
     * @param movement movement to execute
     * @ass2
     */
    public void processMovement(Movement movement) {
        if (movement instanceof ShipMovement) {
            // If the movement is a ShipMovement
            ShipMovement shipMovement = (ShipMovement) movement;
            Ship ship = shipMovement.getShip();
            if (shipMovement.getDirection() == MovementDirection.INBOUND) {
                shipQueue.add(ship);
            } else { // Direction == OUTBOUND
                for (Quay q : quays) {
                    if (q.getShip() == ship) {
                        // Load cargo onto ship before departure
                        List<Cargo> loaded = new ArrayList<>();
                        for (Cargo cargo : storedCargo) {
                            if (ship.canLoad(cargo)) {
                                ship.loadCargo(cargo);
                                loaded.add(cargo);
                            }
                        }
                        storedCargo.removeAll(loaded);
                        // depart ship
                        q.shipDeparts();
                        break; // no need to continue as a ship should {can}
                        // only be in one quay at once.
                    }
                }
            }
        } else if (movement instanceof CargoMovement) {
            // If the movement is a CargoMovement
            CargoMovement cargoMovement = (CargoMovement) movement;
            List<Cargo> cargo = cargoMovement.getCargo();
            if (cargoMovement.getDirection() == MovementDirection.INBOUND) {
                storedCargo.addAll(cargo);
            } else { // Direction == OUTBOUND
                // Remove all cargo with the set ID
                storedCargo.removeAll(cargo);
            }
        }
        for (StatisticsEvaluator eval : evaluators) {
            eval.onProcessMovement(movement);
        }
    }

    /**
     * Adds the given statistics evaluator to the port's list of evaluators.
     * <p>
     * If the port already has an evaluator of that type, no action should be
     * taken.
     *
     * @param eval statistics evaluator to add to the port
     * @ass2
     */
    public void addStatisticsEvaluator(StatisticsEvaluator eval) {
        for (StatisticsEvaluator evaluator : evaluators) {
            if (evaluator.getClass().isInstance(eval)) {
                return;
            }
        }
        evaluators.add(eval);
    }

    /**
     * Returns the name of this port.
     *
     * @return port's name
     * @ass1
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the time since simulation started.
     *
     * @return time in minutes
     * @ass2
     */
    public long getTime() {
        return time;
    }

    /**
     * Returns a list of all quays associated with this port.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     * <p>
     * The order in which quays appear in this list should be the same as
     * the order in which they were added by calling {@link #addQuay(Quay)}.
     *
     * @return all quays
     * @ass1
     */
    public List<Quay> getQuays() {
        return new ArrayList<>(this.quays);
    }

    /**
     * Returns the cargo stored in warehouses at this port.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     *
     * @return port cargo
     * @ass1
     */
    public List<Cargo> getCargo() {
        return new ArrayList<>(this.storedCargo);
    }

    /**
     * Returns the queue of ships waiting to be docked at this port.
     *
     * @return port's queue of ships
     * @ass2
     */
    public ShipQueue getShipQueue() {
        return shipQueue;
    }

    /**
     * Returns the queue of movements waiting to be processed.
     *
     * @return movements queue
     * @ass2
     */
    public PriorityQueue<Movement> getMovements() {
        return movements;
    }

    /**
     * Returns the list of evaluators at the port.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     *
     * @return the ports evaluators
     * @ass2
     */
    public List<StatisticsEvaluator> getEvaluators() {
        return evaluators;
    }

    /**
     * Adds a quay to the ports control.
     *
     * @param quay the quay to add
     * @ass1
     */
    public void addQuay(Quay quay) {
        this.quays.add(quay);
    }

    /**
     * Advances the simulation by one minute.
     * <p>
     * On each call to {@code elapseOneMinute()}, the following actions should be completed by
     * the port in order:
     * <ol>
     * <li>Advance the simulation time by 1</li>
     * <li>If the time is a multiple of 10, attempt to bring a ship from the
     * ship queue to any empty quay that matches the requirements from {@link Ship#canDock(Quay)}.
     * The ship should only be docked to one quay.
     * </li>
     * <li>If the time is a multiple of 5, all quays must unload the cargo from ships
     * docked (if any) and add it to warehouses at the port (the Port's list of stored cargo)</li>
     * <li>All movements stored in the queue whose action time is equal to the current time
     * should be processed by {@link #processMovement(Movement)}</li>
     * <li>Call {@link StatisticsEvaluator#elapseOneMinute()} on all statistics evaluators</li>
     * </ol>
     *
     * @ass2
     */
    @Override
    public void elapseOneMinute() {
        // advance simulation time
        time += 1;

        // shipQueue processing
        if (time % 10 == 0) {
            Ship queuedShip = shipQueue.peek();
            for (Quay quay : quays) {
                if (quay.isEmpty() && queuedShip != null && queuedShip.canDock(quay)) {
                    quay.shipArrives(shipQueue.poll());
                    break;
                }
            }
        }

        // Quay processing
        if (time % 5 == 0) {
            for (Quay quay : quays) {
                if (!quay.isEmpty()) {
                    try {
                        if (quay instanceof ContainerQuay) {
                            ContainerShip ship = (ContainerShip) quay.getShip();
                            storedCargo.addAll(ship.unloadCargo());
                        } else if (quay instanceof BulkQuay) {
                            BulkCarrier ship = (BulkCarrier) quay.getShip();
                            storedCargo.add(ship.unloadCargo());
                        }
                    } catch (NoSuchCargoException e) {
                        // Ignore
                    }
                }
            }
        }

        // Movement processing
        Movement movement;
        // Check if movements has an element and if its time is the same as
        // current. movements are ordered by their action time.
        while ((movement = movements.peek()) != null && movement.getTime() == time) {
            processMovement(movements.poll());
        }

        // Evaluators processing
        for (StatisticsEvaluator eval : evaluators) {
            eval.elapseOneMinute();
        }
    }

    /**
     * Returns the machine-readable string representation of this Port.
     * <p>
     * The format of the string to return is
     * <pre>
     * Name
     * Time
     * numCargo
     * EncodedCargo
     * EncodedCargo...
     * numShips
     * EncodedShip
     * EncodedShip...
     * numQuays
     * EncodedQuay
     * EncodedQuay...
     * ShipQueue:numShipsInQueue:shipID,shipID,...
     * StoredCargo:numCargo:cargoID,cargoID,...
     * Movements:numMovements
     * EncodedMovement
     * EncodedMovement...
     * Evaluators:numEvaluators:EvaluatorSimpleName,EvaluatorSimpleName,...
     * </pre>
     * Where:
     * <ul>
     *   <li>Name is the name of the Port</li>
     *   <li>Time is the time elapsed since the simulation started</li>
     *   <li>numCargo is the total number of cargo in the simulation</li>
     *   <li>If present (numCargo &gt; 0): EncodedCargo is the encoded representation of each
     *   individual cargo in the simulation </li>
     *   <li>numShips is the total number of ships in the simulation</li>
     *   <li>If present (numShips &gt; 0): EncodedShip is the encoded representation of each
     *   individual ship encoding in the simulation</li>
     *   <li>numQuays is the total number of quays in the Port</li>
     *   <li>If present (numQuays &gt; 0): EncodedQuay is the encoded representation of each
     *   individual quay in the simulation</li>
     *   <li>numShipsInQueue is the total number of ships in the ship queue
     *   in the port</li>
     *   <li>If present (numShipsInQueue &gt; 0): shipID is each ship's ID in the aforementioned
     *   queue</li>
     *   <li>numCargo is the total amount of stored cargo in the Port</li>
     *   <li>If present (numCargo &gt; 0): cargoID is each cargo's ID in the stored cargo list of
     *   Port</li>
     *   <li>numMovements is the number of movements in the list of movements
     *   in Port</li>
     *   <li>If present (numMovements &gt; 0): EncodedMovement is the encoded representation of each
     *   individual Movement in the aforementioned list</li>
     *   <li>numEvaluators is the number of statistics evaluators in the Port evaluators list</li>
     *   <li>If present (numEvaluators &gt; 0): EvaluatorSimpleName is the name given by
     *   {@link Class#getSimpleName()} for each evaluator in the aforementioned list separated
     *   by a comma</li>
     *   <li>Each line is separated by a {@link System#lineSeparator()}</li>
     * </ul>
     * <p>
     * For example the minimum / default encoding would be:
     * <pre>
     * PortName
     * 0
     * 0
     * 0
     * 0
     * ShipQueue:0:
     * StoredCargo:0:
     * Movements:0
     * Evaluators:0:
     * </pre>
     *
     * @return encoded string representation of this Port
     * @ass2
     */
    public String encode() {
        StringJoiner lists = new StringJoiner(",");
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        joiner.add(this.name);
        joiner.add(String.valueOf(this.time));
        joiner.add(String.valueOf(Cargo.getCargoRegistry().size()));
        for (Cargo cargo : Cargo.getCargoRegistry().values()) {
            joiner.add(cargo.encode());
        }
        joiner.add(String.valueOf(Ship.getShipRegistry().size()));
        for (Ship ship : Ship.getShipRegistry().values()) {
            joiner.add(ship.encode());
        }
        joiner.add(String.valueOf(this.quays.size()));
        for (Quay quay : this.quays) {
            joiner.add(quay.encode());
        }
        joiner.add(this.shipQueue.encode());

        for (Cargo cargo : this.storedCargo) {
            lists.add(String.valueOf(cargo.getId()));
        }
        joiner.add("StoredCargo:" + this.storedCargo.size() + ":" + lists);
        joiner.add("Movements:" + this.movements.size());
        for (Movement movement : this.movements) {
            joiner.add(movement.encode());
        }
        lists = new StringJoiner(",");
        for (StatisticsEvaluator evaluator : this.evaluators) {
            lists.add(evaluator.getClass().getSimpleName());
        }

        joiner.add("Evaluators:" + this.evaluators.size() + ":" + lists);
        return joiner.toString();
    }

    /**
     * Creates a port instance by reading various ship, quay, cargo, movement
     * and evaluator entities from the given reader.
     * <p>
     * The provided file should be in the format:
     * <pre>
     * Name
     * Time
     * numCargo
     * EncodedCargo
     * EncodedCargo...
     * numShips
     * EncodedShip
     * EncodedShip...
     * numQuays
     * EncodedQuay
     * EncodedQuay...
     * ShipQueue:NumShipsInQueue:shipID,shipId
     * StoredCargo:numCargo:cargoID,cargoID
     * Movements:numMovements
     * EncodedMovement
     * EncodedMovement...
     * Evaluators:numEvaluators:EvaluatorSimpleName,EvaluatorSimpleName
     * </pre>
     * As specified by {@link #encode()}
     * <p>
     * The encoded string is invalid if any of the following conditions are true:
     * <ul>
     * <li>The time is not a valid long (i.e. cannot be parsed by
     * {@link Long#parseLong(String)}).</li>
     * <li>The number of cargo is not an integer (i.e. cannot be parsed by
     * {@link Integer#parseInt(String)}).</li>
     * <li>The number of cargo to be read in does not match the number
     * specified above. (ie. too many / few encoded cargo following the
     * number)</li>
     * <li>An encoded cargo line throws a {@link BadEncodingException}</li>
     * <li>The number of ships is not an integer (i.e. cannot be parsed by
     * {@link Integer#parseInt(String)}).</li>
     * <li>The number of ship to be read in does not match the number
     * specified above. (ie. too many / few encoded ships following the
     * number)</li>
     * <li>An encoded ship line throws a {@link BadEncodingException}</li>
     * <li>The number of quays is not an integer (i.e. cannot be parsed by
     * {@link Integer#parseInt(String)}).</li>
     * <li>The number of quays to be read in does not match the number
     * specified above. (ie. too many / few encoded quays following the
     * number)</li>
     * <li>An encoded quay line throws a {@link BadEncodingException}</li>
     * <li>The shipQueue does not follow the last encoded quay</li>
     * <li>The number of ships in the shipQueue is not an integer (i.e. cannot
     * be parsed by {@link Integer#parseInt(String)}).</li>
     * <li>The imoNumber of the ships in the shipQueue are not valid longs.
     * (i.e. cannot be parsed by {@link Long#parseLong(String)}).</li>
     * <li>Any imoNumber read does not correspond to a valid ship in the
     * simulation</li>
     * <li>The storedCargo does not follow the encoded shipQueue</li>
     * <li>The number of cargo in the storedCargo is not an integer (i.e. cannot
     * be parsed by {@link Integer#parseInt(String)}).</li>
     * <li>The id of the cargo in the storedCargo are not valid Integers.
     * (i.e. cannot be parsed by {@link Integer#parseInt(String)}).</li>
     * <li>Any cargo id read does not correspond to a valid cargo in the
     * simulation</li>
     * <li>The movements do not follow the encoded storedCargo</li>
     * <li>The number of movements is not an integer (i.e. cannot be parsed
     * by {@link Integer#parseInt(String)}).</li>
     * <li>The number of movements to be read in does not match the number
     * specified above. (ie. too many / few encoded movements following the
     * number)</li>
     * <li>An encoded movement line throws a {@link BadEncodingException}</li>
     * <li>The evaluators do not follow the encoded movements</li>
     * <li>The number of evaluators is not an integer (i.e. cannot be parsed
     * by {@link Integer#parseInt(String)}).</li>
     * <li>The number of evaluators to be read in does not match the number
     * specified above. (ie. too many / few encoded evaluators following the
     * number)</li>
     * <li>An encoded evaluator name does not match any of the possible evaluator classes</li>
     * <li>If any of the following lines are missing:
     *      <ol>
     *          <li>Name</li>
     *          <li>Time</li>
     *          <li>Number of Cargo</li>
     *          <li>Number of Ships</li>
     *          <li>Number of Quays</li>
     *          <li>ShipQueue</li>
     *          <li>StoredCargo</li>
     *          <li>Movements</li>
     *          <li>Evaluators</li>
     *      </ol>
     * </li>
     * </ul>
     *
     * @param reader reader from which to load all info
     * @return port created by reading from given reader
     * @throws IOException          if an IOException is encountered when reading from
     *                              the reader
     * @throws BadEncodingException if the reader reads a line that does not
     *                              adhere to the rules above indicating that the contents of the
     *                              reader are invalid
     * @ass2
     */
    public static Port initialisePort(Reader reader) throws IOException,
        BadEncodingException {
        BufferedReader portReader = new BufferedReader(reader);
        String name = portReader.readLine();
        if (name == null) {
            throw new BadEncodingException("Expected a name for the port");
        }
        String timeLine = portReader.readLine();
        long time;
        try {
            time = Long.parseLong(timeLine);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("The current time of the port "
                + "should be a long");
        }

        int numCargo;
        try {
            numCargo = Integer.parseInt(portReader.readLine());
        } catch (NumberFormatException e) {
            throw new BadEncodingException("The number of cargo should be an "
                + "integer");
        }
        String cargoLine;
        for (int i = 0; i < numCargo; i++) {
            cargoLine = portReader.readLine();
            if (cargoLine != null && (cargoLine.contains("BulkCargo") || cargoLine.contains(
                "Container"))) {
                Cargo.fromString(cargoLine);
            } else {
                throw new BadEncodingException("Expected valid encoded cargo "
                    + "line " + "here: " + cargoLine);
            }
        }

        int numShips;
        try {
            numShips = Integer.parseInt(portReader.readLine());
        } catch (NumberFormatException e) {
            throw new BadEncodingException("The number of ships should be an "
                + "integer");
        }

        String shipLine;
        for (int i = 0; i < numShips; i++) {
            shipLine = portReader.readLine();
            if (shipLine != null && (shipLine.contains("BulkCarrier") || shipLine.contains(
                "ContainerShip"))) {
                Ship.fromString(shipLine);
            } else {
                throw new BadEncodingException("Expected valid encoded ship "
                    + "line here: " + shipLine);
            }
        }

        int numQuay;
        try {
            numQuay = Integer.parseInt(portReader.readLine());
        } catch (NumberFormatException e) {
            throw new BadEncodingException("The number of quays should be an "
                + "integer");
        }

        String quayLine;
        List<Quay> quays = new ArrayList<>();
        for (int i = 0; i < numQuay; i++) {
            quayLine = portReader.readLine();
            if (quayLine != null && quayLine.contains("Quay")) {
                quays.add(Quay.fromString(quayLine));
            } else {
                throw new BadEncodingException("Expected valid encoded quay "
                    + "line here: " + quayLine);
            }
        }

        String shipQueueLine = portReader.readLine();
        if (shipQueueLine == null) {
            throw new BadEncodingException("Expected encoded ship queue here");
        }
        ShipQueue queue = ShipQueue.fromString(shipQueueLine);

        String storedCargoLine = portReader.readLine();
        if (storedCargoLine == null) {
            throw new BadEncodingException("Expected encoded ship queue here");
        }
        String[] storedCargoEncoded = storedCargoLine.split(":", -1);
        if (!(storedCargoEncoded[0].equals("StoredCargo"))) {
            throw new BadEncodingException("Expected encoded stored cargo "
                + "info here: " + storedCargoEncoded[0]);
        }
        int numStoredCargo;
        try {
            numStoredCargo = Integer.parseInt(storedCargoEncoded[1]);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("The number of cargo stored should"
                + " be an integer");
        }

        String[] storedCargo = storedCargoEncoded[2].split(",");
        List<Integer> decodedCargo = new ArrayList<>();
        if (numStoredCargo != storedCargo.length && numStoredCargo > 0) {
            throw new BadEncodingException("The number of cargo in the stored"
                + " cargo should be a equal to the number encoded");
        }
        try {
            for (int i = 0; i < storedCargo.length && numStoredCargo > 0; i++) {
                decodedCargo.add(Integer.parseInt(storedCargo[i]));
            }
        } catch (NumberFormatException e) {
            throw new BadEncodingException("Stored cargo in queue should be "
                + "represented by its id number");
        }

        // check all stored cargo exist
        for (Integer cargoId : decodedCargo) {
            if (!Cargo.cargoExists(cargoId)) {
                throw new BadEncodingException("This cargo in storage does "
                    + "not exist in this system: " + cargoId);
            }
        }

        //Create list of cargo
        List<Cargo> cargoToAdd = new ArrayList<>();
        for (Integer cargo : decodedCargo) {
            try {
                cargoToAdd.add(Cargo.getCargoById(cargo));
            } catch (NoSuchCargoException e) {
                //unreachable
            }

        }
        // created base port without additions
        Port port = new Port(name, time, queue, quays, cargoToAdd);
        String movementsLine = portReader.readLine();
        if (movementsLine == null) {
            throw new BadEncodingException("Expected movements line");
        }
        String[] movements = movementsLine.split(":", -1);
        if (!movements[0].equals("Movements")) {
            throw new BadEncodingException("Expected information about "
                + "movements here: " + movements[0]);
        }

        int numMovements;
        try {
            numMovements = Integer.parseInt(movements[1]);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("Number of movements in queue "
                + "should be an integer");
        }
        String movementLine;

        for (int i = 0; i < numMovements; i++) {
            movementLine = portReader.readLine();
            if (movementLine == null || !movementLine.contains("Movement")) {
                throw new BadEncodingException("Not a valid movement line: " + movementLine);
            }
            try {
                port.addMovement(readMovement(movementLine));
            } catch (IllegalArgumentException e) {
                throw new BadEncodingException("Not a valid movement line: " + movementLine, e);
            }
        }

        String evaluatorsLine = portReader.readLine();
        if (evaluatorsLine == null) {
            throw new BadEncodingException("Expected movements line");
        }
        String[] evaluators = evaluatorsLine.split(":", -1);
        if (!(evaluators[0].equals("Evaluators"))) {
            throw new BadEncodingException("Expected a valid encoded "
                + "evaluators");
        }

        int numEvaluators;
        try {
            numEvaluators = Integer.parseInt(evaluators[1]);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("Number of evaluators should be an"
                + " integer, but was: " + evaluators[1]);
        }

        String[] encodedEvaluators = evaluators[2].split(",");
        if (numEvaluators != encodedEvaluators.length && numEvaluators > 0) {
            throw new BadEncodingException("The number of evaluators to add "
                + "to the port should be a equal to the number encoded");
        }
        for (int i = 0; i < encodedEvaluators.length && numEvaluators > 0; i++) {
            port.addStatisticsEvaluator(createEvaluator(encodedEvaluators[i],
                port));
        }

        return port;
    }

    // Creates a movement from a string by calling the appropriate fromString
    // method
    private static Movement readMovement(String string) throws BadEncodingException {
        Movement movement = null;
        String[] splitMovement = string.split(":");
        if (splitMovement[0].equals("CargoMovement")) {
            movement = CargoMovement.fromString(string);
        } else if (splitMovement[0].equals("ShipMovement")) {
            movement = ShipMovement.fromString(string);
        } else {
            throw new BadEncodingException(
                "Movement of type " + splitMovement[0] + "does not exist");
        }
        return movement;
    }

    // Creates a new StatisticsEvaluator from a string
    private static StatisticsEvaluator createEvaluator(String string, Port port)
        throws BadEncodingException {
        StatisticsEvaluator evaluator;
        switch (string) {
            case "CargoDecompositionEvaluator":
                return new CargoDecompositionEvaluator();
            case "QuayOccupancyEvaluator":
                return new QuayOccupancyEvaluator(port);
            case "ShipFlagEvaluator":
                return new ShipFlagEvaluator();
            case "ShipThroughputEvaluator":
                return new ShipThroughputEvaluator();
            default:
                throw new BadEncodingException("The evaluator specified does "
                    + "not exist: " + string);
        }

    }

}
