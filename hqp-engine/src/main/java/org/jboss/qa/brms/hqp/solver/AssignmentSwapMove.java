/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.qa.brms.hqp.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;
import org.jboss.qa.brms.hqp.domain.Job;
import org.jboss.qa.brms.hqp.domain.Machine;

/**
 *
 * @author rsynek
 */
public class AssignmentSwapMove implements Move {
    
    private Job jobLeft;
    private Job jobRight;

    public AssignmentSwapMove(Job left, Job right) {
        jobLeft = left;
        jobRight = right;
    }
    
    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return !jobLeft.equals(jobRight) 
                && jobLeft.getNodes().contains(jobRight.getAssignedNode()) 
                && jobRight.getNodes().contains(jobLeft.getAssignedNode());
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new AssignmentSwapMove(jobRight, jobLeft);
    }

    public void doMove(ScoreDirector scoreDirector) {
        Machine left = jobLeft.getAssignedNode();
        Machine right = jobRight.getAssignedNode();
        
        scoreDirector.beforeVariableChanged(jobLeft, "assignedNode");
        jobLeft.setAssignedNode(right);
        scoreDirector.afterVariableChanged(jobLeft, "assignedNode");
        
        scoreDirector.beforeVariableChanged(jobRight, "assignedNode");
        jobRight.setAssignedNode(left);
        scoreDirector.afterVariableChanged(jobRight, "assignedNode");
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(jobLeft, jobRight);
    }

    public Collection<? extends Object> getPlanningValues() {
        return Arrays.asList(jobLeft.getAssignedNode(), jobRight.getAssignedNode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AssignmentSwapMove other = (AssignmentSwapMove) obj;
        if (this.jobLeft != other.jobLeft && (this.jobLeft == null || !this.jobLeft.equals(other.jobLeft))) {
            return false;
        }
        if (this.jobRight != other.jobRight && (this.jobRight == null || !this.jobRight.equals(other.jobRight))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.jobLeft != null ? this.jobLeft.hashCode() : 0);
        hash = 97 * hash + (this.jobRight != null ? this.jobRight.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return "swap: " + jobLeft + " <=> " + jobRight;
    }
}
