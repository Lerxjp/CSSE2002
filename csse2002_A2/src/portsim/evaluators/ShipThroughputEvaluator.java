package portsim.evaluators;

import portsim.movement.Movement;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;
import portsim.ship.Ship;

import java.util.HashMap;
import java.util.Map;

/**
 * Gathers data on how many ships pass through the port over time.
 * <p>
 * This evaluator only counts ships that have passed through the port in the last hour (60 minutes)
 * <p>
 * <b>Note: </b> The Javadoc for this class is intentionally vague to provide you with an
 * opportunity to determine for yourself how to best implement the functionality specified.
 *
 * @ass2
 */
public class ShipThroughputEvaluator extends StatisticsEvaluator {
    /**
     * The number of ships that have entered and left the port
     */
    private Map<Ship, Long> shipsThrough;

    /**
     * Constructs a new ShipThroughputEvaluator.
     * <p>
     * Immediately after creating a new ShipThroughputEvaluator, {@link #getThroughputPerHour()}
     * should return 0.
     *
     * @ass2
     */
    public ShipThroughputEvaluator() {
        super();
        this.shipsThrough = new HashMap<>();
    }

    /**
     * Return the number of ships that have passed through the port in the
     * last 60 minutes.
     *
     * @return ships throughput
     * @ass2
     */
    public int getThroughputPerHour() {
        return shipsThrough.size();
    }

    /**
     * Updates the internal count of ships that have passed through the port using the given
     * movement.
     * <p>
     * If the movement is not an {@code OUTBOUND ShipMovement}, this method returns
     * immediately without taking any action.
     * <p>
     * Otherwise, the internal state of this evaluator should be modified such that
     * {@link #getThroughputPerHour()} should return a value 1 more than before this method was
     * called. e.g. If the following code and output occurred over a program execution:<br>
     * <table border="1"><caption>Example of behaviour</caption>
     *     <tr>
     *         <th>Java method call</th>
     *         <th>Returned value</th>
     *     </tr>
     *     <tr>
     *         <td>{@code getThroughputPerHour()}</td>
     *         <td>1</td>
     *     </tr>
     *     <tr>
     *         <td>{@code onProcessMovement(validMovement)}</td>
     *         <td>void</td>
     *     </tr>
     *     <tr>
     *         <td>{@code getThroughputPerHour()}</td>
     *         <td>2</td>
     *     </tr>
     * </table>
     * <p>
     * Where {@code validMovement} is an OUTBOUND ShipMovement.
     *
     * @param movement movement to read
     * @ass2
     */
    @Override
    public void onProcessMovement(Movement movement) {
        if (movement instanceof ShipMovement) {
            ShipMovement shipMovement = (ShipMovement) movement;
            if (shipMovement.getDirection() == MovementDirection.OUTBOUND) {
                shipsThrough.put(shipMovement.getShip(), this.getTime());
            }
        }
    }

    /**
     * Simulate a minute passing.
     * The time since the evaluator was created should be incremented by one.
     * <p>
     * If it has been more than 60 minutes since a ship exited the port, it
     * should no longer be counted towards the count returned by {@link #getThroughputPerHour()}.
     *
     * @ass2
     */
    @Override
    public void elapseOneMinute() {
        super.elapseOneMinute();
        shipsThrough.values().removeIf(value -> value + 60 < this.getTime());
    }
}
