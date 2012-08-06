/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.qa.brms.hqp.solver;

import java.util.Comparator;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.jboss.qa.brms.hqp.domain.Job;

/**
 *
 * @author rsynek
 */
public class JobComparator implements Comparator<Job> {

    public int compare(Job o1, Job o2) {
        return new CompareToBuilder()
                .append(o2.getNodes().size(), o1.getNodes().size())
                .append(o1.getName(), o2.getName())
                .toComparison();
    }
    
}
