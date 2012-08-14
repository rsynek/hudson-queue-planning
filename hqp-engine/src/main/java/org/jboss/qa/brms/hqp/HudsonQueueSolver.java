package org.jboss.qa.brms.hqp;

import org.jboss.qa.brms.hqp.domain.HudsonQueue;

/**
 * Hudson Queue Planning API interface.
 * @author rsynek
 */
public interface HudsonQueueSolver {

    /**
     * Gets solution from the solver.
     * Assigned node is never null, in case particular job has not assigned any node, NOT-ASSIGNED node is provided.
     * @return actual best solution
     */
    HudsonQueue getSolution();

    /**
     * Starts the solver in separate thread.
     * @param queue input queue to be solved
     */
    void start(HudsonQueue queue);

    /**
     * Stops the solver immediately.
     */
    void stop();

    /**
     * Updates the queue.
     * In case the solver is not running any more, restarts it.
     * @param queue actual queue to be merged with previous the solver is working on.
     */
    void update(HudsonQueue queue);
    
    /**
     * How many jobs assigned ratio (actual solution vs FIFO).
     * @return difference between FIFO and this solution; higher is better -> it tells how much more jobs have been assigned
     */
    int getRatio();
}
