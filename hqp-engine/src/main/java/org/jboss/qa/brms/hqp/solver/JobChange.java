package org.jboss.qa.brms.hqp.solver;

import java.util.*;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solver.ProblemFactChange;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;
import org.jboss.qa.brms.hqp.domain.Job;
import org.jboss.qa.brms.hqp.domain.Machine;
import org.jboss.qa.brms.hqp.domain.SlaveExecutor;

/**
 * Job queue merging.
 * @author rsynek
 */
public class JobChange implements ProblemFactChange {

    private HudsonQueue newQueue;

    public JobChange(HudsonQueue newQueue) {
        this.newQueue = newQueue;
    }

    /**
     * Change of facts - updates the queue inside solver with the actual one.
     * New jobs are added, Old jobs updated, jobs that are no more present in actual queue are removed from solver.
     * Similar merging is done with available executors.
     * @param scoreDirector scoreDirector holding working solution.
     */
    @Override
    public void doChange(ScoreDirector scoreDirector) {      
        HudsonQueue queue = (HudsonQueue) scoreDirector.getWorkingSolution();
        
        //first, update situation about nodes & executors
        Set<SlaveExecutor> removed = updateExecutors(scoreDirector, newQueue, queue);

        //update jobs
        Iterator<Job> it = queue.getJobQueue().iterator();
        while (it.hasNext()) {
            Job actual = it.next();

            int i = newQueue.getJobQueue().indexOf(actual);
            if (i < 0) { //not found in new queue => remove it
                scoreDirector.beforeEntityRemoved(actual);
                it.remove();
                scoreDirector.afterEntityRemoved(actual);
            } else { //found => merge it               
                Job newJob = newQueue.getJobQueue().get(i);

                scoreDirector.beforeVariableChanged(actual, "nodes");
                actual.setNodes(newJob.getNodes());
                scoreDirector.afterVariableChanged(actual, "nodes");
                scoreDirector.beforeVariableChanged(actual, "priority");
                actual.setPriority(newJob.getPriority());
                scoreDirector.afterVariableChanged(actual, "priority");
                scoreDirector.beforeVariableChanged(actual, "timeDiff");
                actual.computeTimeDiff(new Date());
                scoreDirector.afterVariableChanged(actual, "timeDiff");

                //if any machine has gone down and it was already assigned to a job, remove it from there.
                if (actual.getAssigned() != null && !actual.getAssigned().equals(SlaveExecutor.UnassignedSlave())) {
                    if (removed.contains(actual.getAssigned())) {
                        scoreDirector.beforeVariableChanged(actual, "assigned");
                        actual.setAssigned(SlaveExecutor.UnassignedSlave());
                        scoreDirector.afterVariableChanged(actual, "assigned");
                    }
                }

                newQueue.getJobQueue().remove(i);
            }
        }

        // rest of jobs are new ones, must be added
        for (Job newJob : newQueue.getJobQueue()) {
            newJob.computeTimeDiff(new Date());
            newJob.getNodes().add(new Machine(Machine.NOT_ASSIGNED));
            newJob.setAssigned(SlaveExecutor.UnassignedSlave());
            scoreDirector.beforeEntityAdded(newJob);
            queue.getJobQueue().add(newJob);
            scoreDirector.afterEntityAdded(newJob);
        }
       
        //slaves to be removed must be removed
        for (SlaveExecutor exec : removed) {    
            scoreDirector.beforeProblemFactRemoved(exec);
            queue.getSlaves().remove(exec);
            scoreDirector.afterProblemFactRemoved(exec);
        }

    }
    
    /**
     * Adds new SlaveExecutors, constructs the set of executors to be removed.
     * -sets of available machines and their executors are constructed from the newQueue
     * -new executors are added as facts to scoreDirector
     * -executors that were in the previous queue and are not in the actual queue are saved to be removed at the end of change.
     */
    private Set<SlaveExecutor> updateExecutors(ScoreDirector sd, HudsonQueue newQueue, HudsonQueue oldQueue) {
        Set<SlaveExecutor> newSlaves = newQueue.getSlaves();
        Set<SlaveExecutor> oldSlaves = oldQueue.getSlaves();

        Set<SlaveExecutor> removed = new HashSet<SlaveExecutor>();
        for(SlaveExecutor oldSlave : oldSlaves) {
            if(!newSlaves.contains(oldSlave)) {
                removed.add(oldSlave);
            }
        }
        
        //new slave executors are added
        for(SlaveExecutor newSlave : newSlaves) {
            if(!oldSlaves.contains(newSlave)) {
                sd.beforeProblemFactAdded(newSlave);
                oldSlaves.add(newSlave);
                sd.afterProblemFactAdded(newSlave);
            }
        }

        return removed;
    }

}
