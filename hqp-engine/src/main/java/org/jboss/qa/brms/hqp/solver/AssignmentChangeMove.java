/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.qa.brms.hqp.solver;

import java.util.Collection;
import java.util.Collections;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;
import org.jboss.qa.brms.hqp.domain.Job;
import org.jboss.qa.brms.hqp.domain.Machine;

/**
 * 
 * @author rsynek
 */
public class AssignmentChangeMove implements Move {

    private Machine node;
    private Job job;
    
    public AssignmentChangeMove(Job job, Machine machine) {
        this.job = job;
        this.node = machine;
    }
    
    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return !job.getAssignedNode().equals(node);//this check is done by move factory: && job.getNodes().contains(node);
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new AssignmentChangeMove(job, job.getAssignedNode());
    }

    public void doMove(ScoreDirector scoreDirector) {
        scoreDirector.beforeVariableChanged(job, "assignedNode");
        job.setAssignedNode(node);
        scoreDirector.afterVariableChanged(job, "assignedNode");
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Collections.singletonList(job);
    }

    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(node);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AssignmentChangeMove other = (AssignmentChangeMove) obj;
        if (this.node != other.node && (this.node == null || !this.node.equals(other.node))) {
            return false;
        }
        if (this.job != other.job && (this.job == null || !this.job.equals(other.job))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.node != null ? this.node.hashCode() : 0);
        hash = 37 * hash + (this.job != null ? this.job.hashCode() : 0);
        return hash;
    }
    
    public String toString() {
        return "change: job " + job + ", node " + node;
    }
}
