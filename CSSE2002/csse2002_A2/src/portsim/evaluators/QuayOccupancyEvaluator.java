package portsim.evaluators;

import portsim.movement.Movement;
import portsim.port.Port;
import portsim.port.Quay;

/**
 * Evaluator to monitor how many quays are currently occupied at the port.
 *
 * @ass2
 */
public class QuayOccupancyEvaluator extends StatisticsEvaluator {
    /**
     * Port to monitor
     */
    private Port port;

    /**
     * Constructs a new QuayOccupancyEvaluator.
     *
     * @param port port to monitor quays
     * @ass2
     */
    public QuayOccupancyEvaluator(Port port) {
        super();
        this.port = port;
    }

    /**
     * Return the number of quays that are currently occupied.
     * <p>
     * A quay is occupied if {@link Quay#isEmpty()} returns false.
     *
     * @return number of quays
     * @ass2
     */
    public int getQuaysOccupied() {
        int count = 0;
        for (Quay q : port.getQuays()) {
            if (!q.isEmpty()) {
                count++;
            }
        }
        return count;
        // For those who like streams
        // return (int) port.getQuays().stream().filter(quay -> !quay.isEmpty()).count();
    }

    /**
     * QuayOccupancyEvaluator does not make use of {@code onProcessMovement()}, so this
     * method can be left empty.
     * <p>
     * Does nothing. This method is not used by this evaluator.
     * @param movement movement to read
     * @ass2
     */
    @Override
    public void onProcessMovement(Movement movement) {
        // do nothing
    }
}
