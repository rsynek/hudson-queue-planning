package org.jboss.qa.brms.hqp.app;

import java.io.File;
import org.jboss.qa.brms.hqp.HudsonQueueSolver;
import org.jboss.qa.brms.hqp.HudsonQueueSolverImpl;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for sample datasets on classpath.
 * Usage: java org.jboss.qa.brms.hqp.app.Samples ['data1', 'data2', 'merge'] timeout [|waiting]
 * @author rsynek
 */
public class SamplesApp extends AbstractApplication {
    
    private static final Logger log = LoggerFactory.getLogger(SamplesApp.class);
    
    public static void main( String[] args )
    {
        long timeout = 1000; 
        long waiting = 500;
        
        try {
            timeout = Long.parseLong(args[1]);
        } catch(Exception ex) {
            throw new IllegalArgumentException("Timeout [msec] must be provided.", ex);
        }
        
        SamplesApp samples = new SamplesApp();
        if("data1".equals(args[0])) {
            samples.runSample1(timeout);
        } else if("data2".equals(args[0])) {
            samples.runSample2(timeout);
        } else if("merge".equals(args[0])) {
            try {
                waiting = Long.parseLong(args[2]);
            } catch(Exception ex) {
                throw new IllegalArgumentException("Waiting time before update [msec] must be provided in merge scenario.", ex);
            }
            samples.runMerge(waiting, timeout);
        } else {
            throw new IllegalArgumentException("Unknown scenario has been chosen. Try one of [data1, data2, merge]");
        }
    }
    
    
    private void runSample1(long timeout) {
        run("/org/jboss/qa/brms/hqp/sample_data_1.json", timeout);
    }
    
    private void runSample2(long timeout) {
        run("/org/jboss/qa/brms/hqp/sample_data_2.json", timeout);
    }
    
    private void runMerge(long waiting, long timeout) {
        if(waiting > timeout) {
            throw new IllegalArgumentException("waiting time cannot be longer than timeout");
        }
        
        HudsonQueueSolver solver = new HudsonQueueSolverImpl();
        
        HudsonQueue queue = io.readJson(new File(
                SamplesApp.class.getResource("/org/jboss/qa/brms/hqp/sample_data_1.json").getFile()));
        solver.start(queue);
        
        try {
            Thread.sleep(waiting);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        
        HudsonQueue newQueue = io.readJson(new File(
                SamplesApp.class.getResource("/org/jboss/qa/brms/hqp/sample_data_1_merge.json").getFile()));
        
        solver.update(newQueue);
        
        try {
            Thread.sleep(timeout - waiting);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        HudsonQueue solution = solver.getSolution();

        printSolution(solution);
        System.out.println("ratio: " + solver.getRatio());
        solver.stop();
    }
    
    private void run(String classpath, long timeout) {
        log.info("start of parsing: " + classpath);
        
        HudsonQueue queue = io.readJson(new File(SamplesApp.class.getResource(classpath).getFile()));        
        
        log.info("file has been parsed successfully");
        run(queue, timeout);
    }
}
