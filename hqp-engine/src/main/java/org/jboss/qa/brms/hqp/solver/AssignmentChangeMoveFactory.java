/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.qa.brms.hqp.solver;

import java.util.ArrayList;
import java.util.List;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.factory.CachedMoveFactory;
import org.drools.planner.core.solution.Solution;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;
import org.jboss.qa.brms.hqp.domain.Job;
import org.jboss.qa.brms.hqp.domain.Machine;

/**
 *
 * @author rsynek
 */
public class AssignmentChangeMoveFactory extends CachedMoveFactory {

    @Override
    public List<Move> createCachedMoveList(Solution solution) {
        List<Move> moves = new ArrayList<Move>();
        HudsonQueue queue = (HudsonQueue) solution;
        for(Job j : queue.getJobQueue()) {
            for(Machine m : j.getNodes()) {
                moves.add(new AssignmentChangeMove(j, m));
            }
        }
        return moves;
    }
    
}
