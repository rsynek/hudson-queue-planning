/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.qa.brms.hqp.solver;

import java.util.Arrays;
import java.util.Collection;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;
import org.jboss.qa.brms.hqp.domain.Job;
import org.jboss.qa.brms.hqp.domain.SlaveExecutor;

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
        return !jobLeft.equals(jobRight);/* 
                && jobLeft.getNodes().contains(jobRight.getAssigned()) 
                && jobRight.getNodes().contains(jobLeft.getAssigned());*/
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new AssignmentSwapMove(jobRight, jobLeft);
    }

    public void doMove(ScoreDirector scoreDirector) {
        SlaveExecutor left = jobLeft.getAssigned();
        SlaveExecutor right = jobRight.getAssigned();
        
        scoreDirector.beforeVariableChanged(jobLeft, "assigned");
        jobLeft.setAssigned(right);
        scoreDirector.afterVariableChanged(jobLeft, "assigned");
        
        scoreDirector.beforeVariableChanged(jobRight, "assigned");
        jobRight.setAssigned(left);
        scoreDirector.afterVariableChanged(jobRight, "assigned");
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(jobLeft, jobRight);
    }

    public Collection<? extends Object> getPlanningValues() {
        return Arrays.asList(jobLeft.getAssigned(), jobRight.getAssigned());
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
