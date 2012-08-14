package org.jboss.qa.brms.hqp.solver;

import java.util.Comparator;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.jboss.qa.brms.hqp.domain.Job;

/**
 * Comparator of planning difficulty. Job with less assignable nodes is more difficult to assign to some node.
 * @author rsynek
 */
public class JobComparator implements Comparator<Job> {

    @Override
    public int compare(Job o1, Job o2) {
        return new CompareToBuilder()
                .append(o2.getNodes().size(), o1.getNodes().size())
                .append(o1.getName(), o2.getName())
                .toComparison();
    }
    
}
