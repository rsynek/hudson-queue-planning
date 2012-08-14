package org.jboss.qa.brms.hqp.app;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import org.jboss.qa.brms.hqp.HudsonQueueSolver;
import org.jboss.qa.brms.hqp.HudsonQueueSolverImpl;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;
import org.jboss.qa.brms.hqp.domain.Job;
import org.jboss.qa.brms.hqp.domain.Machine;
import org.jboss.qa.brms.hqp.io.JsonFileSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rsynek
 */
public class AbstractApplication {
    protected JsonFileSerializer io = new JsonFileSerializer();
    
    protected void run(HudsonQueue queue, long timeout) {        
        HudsonQueueSolver solver = new HudsonQueueSolverImpl();
        
        solver.start(queue);
  
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException ex) {  
            throw new RuntimeException(ex);
        }
        
        HudsonQueue solvedQueue = (HudsonQueue) solver.getSolution();
 
        printSolution(solvedQueue);
        System.out.println("ratio: " + solver.getRatio());
        solver.stop();
    }
    
    public void printSolution(HudsonQueue solution) {
        System.out.println(solution);
        System.out.println();
        Set<Machine> nodes = solution.getAllNodes();
        Collections.sort(solution.getJobQueue(), new Comparator<Job>() {
            
            @Override
            public int compare(Job o1, Job o2) {
                return (int) (o1.getInQueueSince() - o2.getInQueueSince());
            }
        });
        System.out.println("job count:" + solution.getJobQueue().size());
        System.out.println("nodes (" + nodes.size() + "):");
        System.out.println(nodes);
    }
}
