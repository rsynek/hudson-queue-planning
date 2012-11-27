package org.jboss.qa.brms.hqp.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSetter;
import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.api.domain.variable.ValueRangeType;
import org.jboss.qa.brms.hqp.solver.JobComparator;

/**
 * Planning entity - job of the hudson queue to be assigned to a machine.
 * @author rsynek
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@PlanningEntity(difficultyComparatorClass=JobComparator.class)
public class Job {
       
    private long id;
    
    private int priority;
    
    private long inQueueSince;
    
    private String name;    
    
    private Set<Machine> nodes = new HashSet<Machine>();
    
    private SlaveExecutor assigned;

    @JsonIgnore
    private long timeDiff;
    
    /**
     * Computes the time difference between actual time and time when the job entered the queue.
     */
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
        this.nodes.removeAll(this.nodes);
        this.nodes.addAll(nodes);
    }
    
    /**
     * Planning variable is set of slave executors - constructed from all available machines.
     * @return actual assigned node.
     */
    @PlanningVariable
    @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "slaves")
    public SlaveExecutor getAssigned() {
        return this.assigned;
    }

    @JsonSetter
    public void setAssigned(String machine) {
        Iterator<Machine> machineIt = getNodes().iterator();
        while(machineIt.hasNext()) {
            Machine m = machineIt.next();
            if(m.getName().equals(machine)) {
                this.assigned = new SlaveExecutor(m);
                return;
            }
        }
        this.assigned = SlaveExecutor.UnassignedSlave();
    }
    
    public void setAssigned(SlaveExecutor assignedNode) {
        this.assigned = assignedNode;
    }
    
    @Override
    public Job clone() {
        Job clone = new Job();
        clone.id = id;
        clone.priority = priority;
        clone.assigned = assigned;
        
        clone.inQueueSince = inQueueSince;
        clone.timeDiff = timeDiff;
        clone.name = name;

        clone.nodes = new HashSet<Machine>(nodes);
        return clone;
    }
    
    //maybe would be better to add name & nodes instead of ID
    public boolean solutionEquals(Object o) {
        return equals(o);
    }
    
    @Override
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name: ").append(name).append("@").append(id).append(", assigned: ").append(assigned).append(", time:")
                .append(getTimeDiff()).append(", priority: ").append(priority).append("\n");
        sb.append(nodes.toString()).append("\n\n");
        return sb.toString();
    }
}
