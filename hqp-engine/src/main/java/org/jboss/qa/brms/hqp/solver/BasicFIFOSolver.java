/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.qa.brms.hqp.solver;

import java.util.*;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;
import org.jboss.qa.brms.hqp.domain.Job;
import org.jboss.qa.brms.hqp.domain.Machine;
import org.jboss.qa.brms.hqp.domain.SlaveExecutor;

/**
 * Only for comparison with Planner's solver. Simulates previous hudson queue algorithm - FIFO
 *
 * @author rsynek
 */
public class BasicFIFOSolver {

    public static int computeUnassigned(HudsonQueue queue) {
        int count = 0;

        HudsonQueue solution = (HudsonQueue) queue.cloneSolution();

        Set<SlaveExecutor> slaves = solution.getSlaves();
        solution.initSolution();

        for (Job job : solution.getJobQueue()) {
            for (SlaveExecutor slave : slaves) {
                if (slave.getMachine().getName().equals(Machine.NOT_ASSIGNED)) {
                    continue;
                }
                
                if (job.getNodes().contains(slave.getMachine())) {
                    slaves.remove(slave);
                    job.setAssigned(slave);
                    break;
                }   
            }
            if (job.getAssigned() == null) {
                count++;
            }
        }

        return count;
    }
}
