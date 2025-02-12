package portsim.evaluators;

import portsim.movement.Movement;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;

import java.util.HashMap;
import java.util.Map;

/**
 * Gathers data on how many ships each country has sent to this port.
 * <p>
 * Stores a mapping of country-of-origin flags to the number of times
 * that flag has been seen in inbound movements.
 *
 * @ass2
 */
public class ShipFlagEvaluator extends StatisticsEvaluator {

    /**
     * Database of how many times each country of origin has appeared at the
     * port.
     */
    private Map<String, Integer> flagDistribution;

    /**
     * Constructs a new ShipFlagEvaluator.
     *
     * @ass2
     */
    public ShipFlagEvaluator() {
        super();
        this.flagDistribution = new HashMap<>();
    }

    /**
     * Return the flag distribution seen at this port.
     *
     * @return flag distribution
     * @ass2
     */
    public Map<String, Integer> getFlagDistribution() {
        return flagDistribution;
    }

    /**
     * Return the number of times the given flag has been seen at the port.
     *
     * @param flag country flag to find in the mapping
     * @return number of times flag seen or 0 if not seen
     * @ass2
     */
    public int getFlagStatistics(String flag) {
        return flagDistribution.getOrDefault(flag, 0);
    }

    /**
     * Updates the internal mapping of ship country flags using the given movement.
     * <p>
     * If the movement is not an {@code INBOUND} movement, this method returns
     * immediately without taking any action.
     * <p>
     * If the movement is not a ShipMovement, this method returns
     * immediately without taking any action.
     * <p>
     * If the movement is an {@code INBOUND} ShipMovement, do the following:
     * <ul>
     *     <li>If the flag has been seen before (exists as a key in the map)
     *     increment that number
     *     </li>
     *     <li>If the flag has not been seen before add as a key in the map
     *     with a corresponding value of 1</li>
     * </ul>
     *
     * @param movement movement to read
     * @ass2
     */
    @Override
    public void onProcessMovement(Movement movement) {
        if (movement instanceof ShipMovement
            && movement.getDirection() == MovementDirection.INBOUND) {
            ShipMovement shipMovement = (ShipMovement) movement;
            String flag = shipMovement.getShip().getOriginFlag();
            // increment if seen else add as 1
            flagDistribution.merge(flag, 1, Integer::sum);
        }
    }
}
