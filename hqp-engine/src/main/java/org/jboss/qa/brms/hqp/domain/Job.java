package org.jboss.qa.brms.hqp.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.api.domain.variable.ValueRangeType;
import org.jboss.qa.brms.hqp.solver.JobComparator;

/**
 *
 * @author rsynek
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@PlanningEntity(difficultyComparatorClass=JobComparator.class)
public class Job {
    
    private long id;
    
    private int priority;
    
    private long inQueueSince;
    
    private String name;    
    
    private Set<Machine> nodes;
    
    private SlaveExecutor assigned;

    @JsonIgnore
    private long timeDiff;
    
    public void computeTimeDiff(Date now) {
        timeDiff = now.getTime() - inQueueSince;
        if(timeDiff <= 0) {
            throw new IllegalStateException("Job [" + name + "@" + id + "] cannot be added into the queue in the future");
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public long getTimeDiff() {      
        return timeDiff;       
    }
    
    public long getInQueueSince() {
        return inQueueSince;
    }

    public void setInQueueSince(long inQueueSince) {
        this.inQueueSince = inQueueSince;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Machine> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Machine> nodes) {
        this.nodes = nodes;
    }
    
    @PlanningVariable
    @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "slaves")
    public SlaveExecutor getAssigned() {
        return this.assigned;
    }

    public void setAssigned(SlaveExecutor assignedNode) {
        this.assigned = assignedNode;
    }
    
    public Job clone() {
        Job clone = new Job();
        clone.id = id;
        clone.priority = priority;
        if(assigned != null) {
            clone.assigned = assigned.clone();
        }
        else { 
            clone.assigned = null;
        }
        
        clone.inQueueSince = inQueueSince;
        clone.timeDiff = timeDiff;
        clone.name = name;
        Set<Machine> clonedNodes = new HashSet<Machine>();
        for(Machine node : nodes) {
            clonedNodes.add(node.clone());
        }
        clone.nodes = clonedNodes;
        return clone;
    }
    
    public boolean solutionEquals(Object o) {
        return equals(o);
    }
    
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Job) {
            Job other = (Job) o;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .isEquals();
        } else {
            return false;
        } 
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }
    
    public int solutionHashCode() {
        HashCodeBuilder hbuilder = new HashCodeBuilder();
        hbuilder.append(name);
        for(Machine m : nodes) {
            hbuilder.append(m);
        }
        hbuilder.append(assigned);
        return hbuilder.toHashCode();
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name: ").append(name).append("@").append(id).append(", assigned: ").append(assigned).append(", time:")
                .append(getTimeDiff()).append(", priority: ").append(priority).append("\n");
        sb.append(nodes.toString()).append("\n\n");
        return sb.toString();
    }
}
