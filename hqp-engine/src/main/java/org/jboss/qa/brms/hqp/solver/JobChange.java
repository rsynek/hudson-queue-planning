/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.qa.brms.hqp.solver;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solver.ProblemFactChange;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;
import org.jboss.qa.brms.hqp.domain.Job;
import org.jboss.qa.brms.hqp.domain.Machine;

/**
 *
 * @author rsynek
 */
public class JobChange implements ProblemFactChange {

    private List<Job> newQueue;
    
    public JobChange(List<Job> newQueue) {
        this.newQueue = newQueue;
    }
    
    @Override
    public void doChange(ScoreDirector scoreDirector) {
        HudsonQueue queue = (HudsonQueue) scoreDirector.getWorkingSolution();
        
        Iterator<Job> it = queue.getJobQueue().iterator();
        while(it.hasNext()) {
            Job actual = it.next();
            
            int i = newQueue.indexOf(actual);
            if(i < 0) { //not found in new queue => remove it
                scoreDirector.beforeEntityRemoved(actual);
                it.remove();
                scoreDirector.afterEntityRemoved(actual);
            } else { //found => merge it               
                Job newJob = newQueue.get(i);
                
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
                if(actual.getAssignedNode() != null && !actual.getAssignedNode().getName().equals(Machine.NOT_ASSIGNED)
                        && !newJob.getNodes().contains(actual.getAssignedNode())) {
                    scoreDirector.beforeVariableChanged(actual, "assignedNode");
                    actual.setAssignedNode(null);
                    scoreDirector.afterVariableChanged(actual, "assignedNode");
                }
    
                newQueue.remove(i);
            }
        }

        // rest of jobs are new ones, must be added
        for(Job newJob : newQueue) {
            newJob.computeTimeDiff(new Date());
            newJob.getNodes().add(new Machine("not-assigned"));
            newJob.setAssignedNode(new Machine("not-assigned"));
            scoreDirector.beforeEntityAdded(newJob);
            queue.getJobQueue().add(newJob);
            scoreDirector.afterEntityAdded(newJob);
        }    
        
    }
    
}
