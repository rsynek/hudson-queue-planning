package org.jboss.qa.brms.hqp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.drools.planner.config.XmlSolverFactory;
import org.drools.planner.core.Solver;
import org.drools.planner.core.score.buildin.hardandsoft.HardAndSoftScore;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;
import org.jboss.qa.brms.hqp.solver.BasicFIFOSolver;
import org.jboss.qa.brms.hqp.solver.JobChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the Hudson Queue Planning API.
 *
 * @author rsynek
 */
public class HudsonQueueSolverImpl implements HudsonQueueSolver {

    private static Logger logger = LoggerFactory.getLogger(HudsonQueueSolverImpl.class);
    
    public static final String config = "/org/jboss/qa/brms/hqp/hudsonQueuePlanningSolverConfig.xml";
    
    private Solver solver;
    
    private ExecutorService exec;
    
    private XmlSolverFactory solverFactory;

    /**
     * Constructor - creates the solver.
     */
    public HudsonQueueSolverImpl() {
        solverFactory = new XmlSolverFactory();
        solverFactory.configure(config);
        solver = solverFactory.buildSolver();
    }

    /**
     * Starts the solver in separate thread.
     * @param queue input queue to be solved
     */
    @Override
    public void start(HudsonQueue queue) {
        queue.initSolution();

        solver.setPlanningProblem(queue);
        
        exec = Executors.newSingleThreadExecutor();
        exec.execute(new Runnable() {

            @Override
            public void run() {
                solver.solve();
            }
        });
    }

    /**
     * Updates the queue.
     * In case the solver is not running any more, restarts it.
     * @param queue actual queue to be merged with previous the solver is working on.
     */
    @Override
    public void update(HudsonQueue queue) {
        if (solver == null || solver.isTerminateEarly()) {
            throw new IllegalStateException("Solver is not initialized, use 'start' before update");
        } else {/* enable it after https://issues.jboss.org/browse/JBRULES-3692 is fixed */
            if(!solver.isSolving()) {
                restartSolver();
            } 
            solver.addProblemFactChange(new JobChange(queue));
        }
        
    }
 
    /**
     * Restarts the solver.
     * This planning never ends, every time update comes, the solver must be restarted.
     */
    private void restartSolver() {
        HudsonQueue last = (HudsonQueue) solver.getBestSolution();
        exec.shutdown();
        
        logger.info("....restarting solver during update....");
        //here is the place to dynamically change the solver configuration
        //...
        
        solver = solverFactory.buildSolver();
        solver.setPlanningProblem(last);       

        exec = Executors.newSingleThreadExecutor();
        exec.execute(new Runnable() {

            @Override
            public void run() {
                solver.solve();
            }
        });
    }
    
    /**
     * Gets solution from the solver.
     * Assigned node is never null, in case particular job has not assigned any node, NOT-ASSIGNED node is provided.
     * @return actual best solution
     */
    @Override
    public HudsonQueue getSolution() {
        if (solver == null || solver.isTerminateEarly()) {
            throw new IllegalStateException("Solver is not initialized, use 'start' before getting solution");
        } else {           
            return (HudsonQueue) solver.getBestSolution().cloneSolution();
        }
    }

    /**
     * Stops the solver immediately.
     */
    @Override
    public void stop() {
        if (solver != null) {
            solver.terminateEarly();
        }
        if(exec != null && !exec.isShutdown()) {
            exec.shutdown();            
        }
    }

    /**
     * How many jobs assigned ratio (actual solution vs FIFO).
     * @return difference between FIFO and this solution; higher is better -> it tells how much more jobs have been assigned
     */
    @Override
    public int getRatio() {
        HardAndSoftScore score = (HardAndSoftScore) solver.getBestSolution().getScore();
        if(score == null) {
            return 0;
        }
        int fifo = BasicFIFOSolver.computeUnassigned((HudsonQueue)solver.getBestSolution());
        
        return  fifo + score.getHardScore();
    }
}
