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

/**
 *
 * @author rsynek
 */
public class AssignmentSwapMoveFactory extends CachedMoveFactory {

    @Override
    public List<Move> createCachedMoveList(Solution solution) {
        List<Move> moves = new ArrayList<Move>();
        HudsonQueue queue = (HudsonQueue) solution;
        for(Job j1 : queue.getJobQueue()) {
            for(Job j2 : queue.getJobQueue()) {
                moves.add(new AssignmentSwapMove(j1, j2));
            }
        }
        return moves;
    }
    
}
