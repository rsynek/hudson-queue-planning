package org.jboss.qa.brms.hqp.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Hudson Executor. Typically, each machine has only one executors, but there are machines with more...
 * Each executor is identified by machine it belongs to and ID (simply assigned from number of free executors).
 * @author rsynek
 */
public class SlaveExecutor {
    
    private int id;
    
    private Machine machine;

    public SlaveExecutor() {}
    
    public SlaveExecutor(Machine m) {
        this(m, 0);
    }
    
    public SlaveExecutor(String machineName) {
        this(new Machine(machineName));
    }
    
    public SlaveExecutor(Machine m, int id) {
        this.machine = m;
        this.id = id;
    }
    
    /**
     * @return special executor which represents state when job has no assigned machine. Null cannot be used because planner 
     * tries to assign facts to entities till their planning variables are null.
     */
    public static SlaveExecutor UnassignedSlave() {
        return new SlaveExecutor(Machine.NOT_ASSIGNED);
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    @Override
    public SlaveExecutor clone() {
        return new SlaveExecutor(machine, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof SlaveExecutor) {
            SlaveExecutor other = (SlaveExecutor) obj;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(machine, other.machine)
                    .isEquals();
        } else {
            return false;
        } 
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(machine)
                .append(id)
                .toHashCode();
    }
    
    @Override
    public String toString() {
        if(machine == null) {
            return "null@" + id;
        }
        return this.machine.toString() + "@" + id;
    }
}
