/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
    @JsonProperty("queue")
    @JsonUnwrapped(enabled=true)    
    private List<Job> jobQueue;
    
    public HudsonQueue() {}
    
    public HudsonQueue(List<Job> jobs) {
        jobQueue = jobs;
    }
    
    public void initSolution() {
        Date now = new Date();
        for(Job job : jobQueue) {
            job.getNodes().add(new Machine(Machine.NOT_ASSIGNED));
            job.setAssignedNode(null);
            job.computeTimeDiff(now);
        }
    }
    
    @JsonIgnore
    public Collection getProblemFacts() {
        Collection facts = new ArrayList<Object>();
        //nothing to add; every job has the list of assignable machines.
        return facts;
    } 
  
    public Solution cloneSolution() {
        HudsonQueue clone = new HudsonQueue();
        clone.score = score;
        List<Job> clonedJobs = new ArrayList<Job>();
        for(Job j : jobQueue) {
            clonedJobs.add(j.clone());
        }
        clone.jobQueue = clonedJobs;
        return clone;
    }

    public HardAndSoftScore getScore() {
        return this.score;
    }

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
    
    /**  useful methods for displaying info about result   **/
    
    @JsonIgnore
    public Set<Machine> getAllNodes() {
        Set<Machine> nodes = new HashSet<Machine>();
        for(Job j : jobQueue) {
            nodes.addAll(j.getNodes());
        }
        return nodes;
    }
    
    @JsonIgnore
    public List<Job> getUnassigned() {
        List<Job> jobs = new ArrayList<Job>();
        for(Job j : jobQueue) {
            if(j.getAssignedNode() != null && j.getAssignedNode().getName().equals("not-assigned")) {
                jobs.add(j);
            }
        }
        return jobs;
    }
    
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
