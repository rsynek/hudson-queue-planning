/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.qa.brms.hqp;

import org.jboss.qa.brms.hqp.domain.HudsonQueue;

/**
 * Hudson Queue Planning API interface.
 * @author rsynek
 */
public interface HudsonQueueSolver {

    /**
     * returns best available solution.
     */
    HudsonQueue getSolution();

    /**
     * starts the planning.
     */
    void start(HudsonQueue queue);

    /**
     * stops the planning
     */
    void stop();

    /**
     * merges actual and previous queue
     * @param queue the actual queue
     */
    void update(HudsonQueue queue);
    
    /**
     * the difference between number of unassigned jobs (old solution - new solution)
     * -> higher number is better; it tells how much more jobs have been assigned
     */
    int getRatio();
}
