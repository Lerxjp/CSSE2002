package portsim.util;

/**
 * Denotes a class whose state changes on every tick of the simulation.
 * @ass2
 */
public interface Tickable {
    /**
     * Method to be called once on every simulation tick.
     * @ass2
     */
    void elapseOneMinute();
}
