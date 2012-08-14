package org.jboss.qa.brms.hqp.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Reprezentation of machine. Important thing is its name (label) and number of free executors.
 * @author rsynek
 */   
public class Machine {
   
    public static final String NOT_ASSIGNED = "not-assigned";
    
    private String name;
    
    private int executors;
    
    private int freeExecutors;
    
    public Machine() {}
    
    public Machine(String name) {
        this(name, 0, 0);
    }

    public Machine(String name, int executors, int freeExecutors) {
        this.name = name;
        this.executors = executors;
        this.freeExecutors = freeExecutors;
    }
    
    public int getExecutors() {
        return executors;
    }

    public void setExecutors(int executors) {
        this.executors = executors;
    }

    public int getFreeExecutors() {
        return freeExecutors;
    }

    public void setFreeExecutors(int freeExecutors) {
        this.freeExecutors = freeExecutors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Machine clone() {
        return new Machine(name, executors, freeExecutors);
    }
    
    public String toString() {
        return name + ":" + freeExecutors;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj instanceof Machine) {
            Machine m = (Machine) obj;
            return new EqualsBuilder()
                    .append(name, m.name)
                    .isEquals();
        }
        else {
            return false;
        }
        
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .toHashCode();
    }
       
}
