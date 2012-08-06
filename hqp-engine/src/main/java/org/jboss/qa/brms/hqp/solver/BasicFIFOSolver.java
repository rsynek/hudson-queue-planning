/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.qa.brms.hqp.solver;

import java.util.*;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;
import org.jboss.qa.brms.hqp.domain.Job;
import org.jboss.qa.brms.hqp.domain.Machine;

/**
 * Only for comparison with Planner's solver.
 * Simulates previous hudson queue algorithm - FIFO
 * @author rsynek
 */
public class BasicFIFOSolver {
    
    public static int computeUnassigned(HudsonQueue queue) {   
        int count = 0;
        HudsonQueue solution = (HudsonQueue) queue.cloneSolution();
        solution.initSolution();
        Set<Machine> usedNodes = new HashSet<Machine>();

        for(Job job : solution.getJobQueue()) {
            for(Machine m : job.getNodes()) {
                if(!usedNodes.contains(m)) {
                    if(m.getName().equals(Machine.NOT_ASSIGNED)) {
                        continue;
                    }
                    usedNodes.add(m);
                    job.setAssignedNode(m);
                    break;
                }
            }
            if(job.getAssignedNode() == null) {
                count++;
            }
        }
        return count;
    }
  
}
