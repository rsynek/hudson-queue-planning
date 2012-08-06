package org.jboss.qa.brms.hqp.io.generate;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;
import org.jboss.qa.brms.hqp.domain.Job;
import org.jboss.qa.brms.hqp.domain.Machine;
import org.jboss.qa.brms.hqp.io.JsonDataIO;

/**
 * Generation of sample inputs.
 * @author rsynek
 */
public class InputGenerator {
    
    //having 100 machines, max 60 of them are assignable to a job
    private int maxMachinesPerJobPercentage = 20;
    //actual time - maxTimeDiffHours hours
    private int maxTimeDiffHours = 36;
    
    private int maxPriority = 100;
    
    private Random rnd = new Random(new Random().nextLong());
    
    private Machine[] machines;
    
    private int jobId = 0;
    
    private Calendar now = Calendar.getInstance();
    
    private JsonDataIO io;
    
    public InputGenerator() {
    }
    
    public InputGenerator(JsonDataIO io) {
        this.io = io;
    }

    public void setMaxMachinesPerJobPercentage(int maxMachinesPerJobPercentage) {
        this.maxMachinesPerJobPercentage = maxMachinesPerJobPercentage;
    }

    public void setMaxPriority(int maxPriority) {
        this.maxPriority = maxPriority;
    }

    public void setMaxTimeDiffHours(int maxTimeDiffHours) {
        this.maxTimeDiffHours = maxTimeDiffHours;
    }
    
    /**
     * Generates machines of real-like names.
     */
    private void generateMachines(int count) {
        String [] firstLabels =  {"soa", "dev", "vmg", "dsp"};
        String [] secondLabels = {"-rhel5", "-rhel6", "-Win2k3", "-Win2k8", "-solaris10"};
        String [] thirdLabels = {"-x86", "-x86_64", "-sparc"};
        
        machines = new Machine[count];
        String label;
        for(int i = 0; i < count; i++) {
            label = firstLabels[rnd.nextInt(firstLabels.length)];
            label += String.format("%02d", rnd.nextInt(100));
            label += secondLabels[rnd.nextInt(secondLabels.length)];
            int third = thirdLabels.length;
            if(!label.contains("solaris")) {
                third--;
            }
            label += thirdLabels[rnd.nextInt(third)];
            machines[i] = new Machine(label);
        }
    }
    
    /**
     * Randomly selects some of generated machines as assignable for a job.
     */
    private Set<Machine> getAssignableMachines() {
        Map<Integer, Machine> assignable = new HashMap<Integer, Machine>();
        int count = rnd.nextInt(machines.length * maxMachinesPerJobPercentage / 100);
        if(count == 0)
            count++;
        
        for(int i = 0; i < count; i++) {
            int next = rnd.nextInt(machines.length);
            if (next == 0) {
                next++;
            }
            
            if(assignable.containsKey(next)) {
                i--;
                continue;
            } else {
                assignable.put(next, machines[next]);
            }
        }
        return new HashSet<Machine>(assignable.values());
    }
    
    private int generateId() {
        return ++jobId;
    }
    
    /**
     * Generates job's waiting milliseconds.
     */
    private long generateTimeDiff() {
        long maxTimeDiffValue = maxTimeDiffHours * 3600 * 1000; //to msec
        long diff = (long) (rnd.nextDouble() * maxTimeDiffValue);

        return now.getTimeInMillis() - diff;
    }
    
    /**
     * Generates list of jobs. Name of job is simply job@ID
     */
    private List<Job> generateJobs(int count) {
        List<Job> jobs = new ArrayList<Job>(count);
        
        for(int i = 0; i < count; i++) {
            Job job = new Job();
            job.setNodes(getAssignableMachines());
            int id = generateId();
            job.setId(id);
            job.setName("job@" + id);    
            job.setPriority(rnd.nextInt(maxPriority));
            job.setInQueueSince(generateTimeDiff());
            jobs.add(job);
        }
        
        return jobs;
    }
    
    /**
     * Generates sample input.
     * @param numJobs number of jobs to be generated.
     * @param numComp number of machines.
     */
    public HudsonQueue generate(int numJobs, int numComp) {
        generateMachines(numComp);
        List<Job> jobs = generateJobs(numJobs);
        HudsonQueue hq = new HudsonQueue(jobs);
        
        return hq;
    }
    
    /**
     * Generates sample input and saves it to the file.
     * @param fileName filename for saving.
     * @param numJobs number of jobs to be generated.
     * @param numComp number of machines to be generated.
     */
    public void generateAndSave(String fileName, int numJobs, int numComp) {
        HudsonQueue queue = generate(numJobs, numComp);
        if(io == null) {
            io = new JsonDataIO();
        }
        String json = io.getJson(queue);
        try {
            FileUtils.writeStringToFile(new File(fileName), json);
        } catch (IOException ex) {
            Logger.getLogger(InputGenerator.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
}
