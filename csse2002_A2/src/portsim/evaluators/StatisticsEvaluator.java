package portsim.evaluators;

import portsim.movement.Movement;
import portsim.util.Tickable;

/**
 * A base class representing an object that gathers and reports data on various aspects of the
 * port's operation.
 * @ass2
 */
public abstract class StatisticsEvaluator implements Tickable {

    /**
     * The current time elapsed since the evaluator was created
     */
    private long time;

    /**
     * Creates a statistics evaluator and initialises the time since the evaluator was created
     * to zero.
     *
     * @ass2
     */
    public StatisticsEvaluator() {
        this.time = 0;
    }

    /**
     * Return the time since the evaluator was created.
     *
     * @return time since created
     * @ass2
     */
    public long getTime() {
        return time;
    }

    /**
     * Read a movement to update the relevant evaluator data.
     * <p>
     * This method is called by the {@link portsim.port.Port#processMovement(Movement)} method.
     *
     * @param movement movement to read
     * @ass2
     */
    public abstract void onProcessMovement(Movement movement);

    /**
     * Simulate a minute passing.
     * The time since the evaluator was created should be incremented
     * by one.
     *
     * @ass2
     */
    @Override
    public void elapseOneMinute() {
        this.time += 1;
    }
}
