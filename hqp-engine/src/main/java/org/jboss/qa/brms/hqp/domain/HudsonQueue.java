package org.jboss.qa.brms.hqp.domain;

import java.util.*;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonUnwrapped;
import org.drools.planner.api.domain.solution.PlanningEntityCollectionProperty;
import org.drools.planner.core.score.buildin.hardandsoft.HardAndSoftScore;
import org.drools.planner.core.solution.Solution;

/**
 * Solution class. Holds list of jobs which should be assigned to machines.
 * @author rsynek
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class HudsonQueue implements Solution<HardAndSoftScore> {
       
    @JsonIgnore
    private HardAndSoftScore score;
    
    @JsonIgnore
    private Set<SlaveExecutor> slaves;
    
    /**
     * These are the planning entities. @PlanningEntityCollectionProperty annotation is on getter.
     */
    @JsonProperty("queue")
    @JsonUnwrapped(enabled=true)    
    private List<Job> jobQueue;
    
    public HudsonQueue() {}
    
    public HudsonQueue(List<Job> jobs) {
        jobQueue = jobs;
    }   
    
    /**
     * Initialization of this queue instance. Does some preprocessing - computes waiting time and adds NOT-ASSIGNED node to
     * each job in queue.
     */
    public void initSolution() {
        Date now = new Date();
        Machine notAssignedMachine = new Machine(Machine.NOT_ASSIGNED);
        for(Job job : jobQueue) {
            job.getNodes().add(notAssignedMachine);
            job.setAssigned(null);
            job.computeTimeDiff(now);          
        }       
        getSlaves();
    }
    
    public Set<SlaveExecutor> getSlaves() {
        if(this.slaves == null) {
            this.slaves = getAllExecutors();
        }
        return this.slaves;
    }

    public void setSlaves(Set<SlaveExecutor> slaveList) {
        this.slaves = slaveList;
    }
    
    /**
     * In this case problem facts are available executors that can be assigned to jobs.
     * @return Collection of facts
     */
    @JsonIgnore
    public Collection getProblemFacts() {
        Collection facts = new ArrayList<Object>();
        facts.addAll(getSlaves());
        return facts;
    } 
  
    @Override
    public Solution cloneSolution() {
        HudsonQueue clone = new HudsonQueue();
        clone.score = score;
        List<Job> clonedJobs = new ArrayList<Job>();
        for(Job j : jobQueue) {
            clonedJobs.add(j.clone());
        }
        clone.jobQueue = clonedJobs;
        
        Set<SlaveExecutor> clonedSlaves = new HashSet<SlaveExecutor>();
        for(SlaveExecutor slave : getSlaves()) {
            clonedSlaves.add(slave.clone());
        }
        clone.slaves = clonedSlaves;
        
        return clone;
    }

    @Override
    public HardAndSoftScore getScore() {
        return this.score;
    }

    @Override
    public void setScore(HardAndSoftScore score) {
        this.score = score;
    }
    
    @PlanningEntityCollectionProperty
    public List<Job> getJobQueue() {
        return this.jobQueue;
    }

    public void setJobQueue(List<Job> jobQueue) {
        this.jobQueue = jobQueue;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        } else {
            final HudsonQueue other = (HudsonQueue) obj;
        
            for (Iterator<Job> it = jobQueue.iterator(), otherIt = other.jobQueue.iterator(); it.hasNext();) {
                Job job = it.next();
                Job otherJob = otherIt.next();

                if (!job.solutionEquals(otherJob)) {
                    return false;
                }
            }
            return true;
        }     
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (Job job : jobQueue) {
            hashCodeBuilder.append(job.solutionHashCode());
        }
        return hashCodeBuilder.toHashCode();
    }
    
    /**
     * Constructs set of all possible machines from known jobs.
     * @return Set of machines  (It does not matter whether the machine has free executors of not).
     */  
    @JsonIgnore
    public Set<Machine> getAllNodes() {
        Set<Machine> nodes = new HashSet<Machine>();
        for(Job j : jobQueue) {
            nodes.addAll(j.getNodes());
        }
        return nodes;
    }
    
    /**
     * Constructs set of all executors. First, set of possible machines is contructed {@link #getAllNodes() getAllNodes}. After
     * that set of executors is created based upon a free executors count from each machine.
     * @return Set of executors.
     */
    @JsonIgnore
    private Set<SlaveExecutor> getAllExecutors() {
        Set<Machine> machines = getAllNodes();
        Set<SlaveExecutor> slaveExecs = new HashSet<SlaveExecutor>();
        for(Machine m : machines) {
            for(int i = 0; i < m.getFreeExecutors(); i++) {
                slaveExecs.add(new SlaveExecutor(m, i));
            }
        }
        slaveExecs.add(SlaveExecutor.UnassignedSlave());
        return slaveExecs;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("------------solution-----------\n");
        if(score != null) {
            sb.append("score: ").append(score.toString()).append("\n");
        }
        for(Job j : jobQueue) {
            sb.append(j.toString());
        }
        return sb.toString();
    }
}
