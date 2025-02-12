package portsim.evaluators;

import portsim.cargo.*;
import portsim.movement.CargoMovement;
import portsim.movement.Movement;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;
import portsim.ship.BulkCarrier;
import portsim.ship.ContainerShip;
import portsim.ship.Ship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects data on what types of cargo are passing through the port. Gathers
 * data on all derivatives of the cargo class.
 * <p>
 * The data gathered is a count of how many times each type of cargo has entered the port.
 * This includes a count of how many times the port has received
 * "BulkCargo" or "Container" class cargo.
 * As well as a count of how many times the port has seen each cargo subclass type
 * ({@link ContainerType} and {@link BulkCargoType}).
 * @ass2
 */
public class CargoDecompositionEvaluator extends StatisticsEvaluator {
    /**
     * Count of how many of each main cargo type there are. Keys are the
     * simple class name of cargo
     */
    private Map<String, Integer> cargoDistribution;
    /**
     * Count of how many of each bulk cargo type there are
     */
    private Map<BulkCargoType, Integer> bulkCargoDistribution;
    /**
     * Count of how many of each container type there are
     */
    private Map<ContainerType, Integer> containerDistribution;

    /**
     * Constructs a new CargoDecompositionEvaluator.
     *
     * @ass2
     */
    public CargoDecompositionEvaluator() {
        super();
        this.cargoDistribution = new HashMap<>();
        this.bulkCargoDistribution = new HashMap<>();
        this.containerDistribution = new HashMap<>();
    }

    /**
     * Returns the distribution of which cargo types that have entered the port.
     *
     * @return cargo distribution map
     * @ass2
     */
    public Map<String, Integer> getCargoDistribution() {
        return cargoDistribution;
    }

    /**
     * Returns the distribution of bulk cargo types that have entered the port.
     *
     * @return bulk cargo distribution map
     * @ass2
     */
    public Map<BulkCargoType, Integer> getBulkCargoDistribution() {
        return bulkCargoDistribution;
    }

    /**
     * Returns the distribution of container cargo types that have entered the
     * port.
     *
     * @return container distribution map
     * @ass2
     */
    public Map<ContainerType, Integer> getContainerDistribution() {
        return containerDistribution;
    }

    /**
     * Updates the internal distributions of cargo types using the given movement.
     * <p>
     * If the movement is not an {@code INBOUND} movement, this method returns
     * immediately without taking any action.
     * <p>
     * If the movement is an {@code INBOUND} movement, do the following:
     * <ul>
     * <li>If the movement is a ShipMovement, Retrieve the cargo from the ships and for each
     * piece of cargo:
     * <ol>
     * <li>If the cargo class (Container / BulkCargo) has been seen before (simple name exists as a
     * key in the cargo map) -&gt; increment that number</li>
     * <li>If the cargo class has not been seen before then add its class simple name as a key in
     * the map with a corresponding value of 1</li>
     * <li>If the cargo type (Value of ContainerType / BulkCargoType) for the given cargo class has
     * been seen before (exists as a key in the map) increment that number</li>
     * <li>If the cargo type (Value of ContainerType / BulkCargoType) for the given cargo class
     * has not been seen before add as a key in the map with a corresponding value of 1</li>
     * </ol></li>
     * <li>If the movement is a CargoMovement, Retrieve the cargo from the movement. For the
     * cargo retrieved:
     * <ol>
     * <li>Complete steps 1-4 as given above for ShipMovement</li>
     * </ol></li>
     * </ul>
     *
     * @param movement movement to read
     * @ass2
     */
    @Override
    public void onProcessMovement(Movement movement) {
        if (movement.getDirection() == MovementDirection.INBOUND) {
            // Handle if cargo is on a ship
            if (movement instanceof ShipMovement) {
                Ship ship = ((ShipMovement) movement).getShip();
                if (ship instanceof ContainerShip) {
                    List<Container> allCargo = ((ContainerShip) ship).getCargo();
                    for (Container cargo : allCargo) {
                        cargoDistribution.merge(cargo.getClass().getSimpleName(),  1,
                            Integer::sum);
                        containerDistribution.merge(cargo.getType(),  1,
                            Integer::sum);
                    }
                } else if (ship instanceof BulkCarrier) {
                    BulkCargo cargo = ((BulkCarrier) ship).getCargo();
                    cargoDistribution.merge(cargo.getClass().getSimpleName(),  1,
                        Integer::sum);
                    bulkCargoDistribution.merge(cargo.getType(), 1,
                        Integer::sum);
                }
                // handle coming into port
            } else if (movement instanceof CargoMovement) {
                List<Cargo> allCargo = ((CargoMovement) movement).getCargo();
                for (Cargo cargo : allCargo) {
                    cargoDistribution.merge(cargo.getClass().getSimpleName(),  1,
                        Integer::sum);
                    if (cargo instanceof Container) {
                        containerDistribution.merge(((Container) cargo).getType(),  1,
                            Integer::sum);
                    } else if (cargo instanceof BulkCargo) {
                        bulkCargoDistribution.merge(((BulkCargo) cargo).getType(), 1,
                            Integer::sum);
                    }

                }
            }
        }
    }
}
