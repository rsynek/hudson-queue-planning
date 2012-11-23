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
    
    protected void runContinually(HudsonQueue queue, long waitingMsecs) {       
        HudsonQueueSolver solver = new HudsonQueueSolverImpl();
        
        solver.start(queue);
  
        HudsonQueue backup = (HudsonQueue) queue.cloneSolution();
        
        double counter = 0.0;
        for(;;) {
            counter++;
            waiting(waitingMsecs);
            HudsonQueue solution = solver.getSolution();

            HudsonQueue updatedQueue = (HudsonQueue) backup.cloneSolution();
            solver.update(updatedQueue);
            
            if(counter % 200 == 0) {
                System.out.println("time [s]: " + counter * ((double)waitingMsecs / 1000));
            //    System.out.println(solution);
            }
        }
    }
    
    private void waiting(long msecs) {
        try {
            Thread.sleep(msecs);
        } catch (InterruptedException ex) {
             throw new RuntimeException(ex) ;
        }
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
