package org.jboss.qa.brms.hqp.app;


import org.jboss.qa.brms.hqp.io.generate.InputGenerator;

/**
 * Generates new input JSON file.
 * usage: java org.jboss.qa.brms.hqp.app.GenerateApp filename jobCount nodeCount
 * 
 */
public class GenerateApp extends AbstractApplication {  
    
    public static void main(String [] args) {
        if(args[0] == null) {
            throw new IllegalArgumentException("Filename must be provided.");
        }
        
        int jobCount;
        try {
            jobCount = Integer.parseInt(args[1]);
        } catch(Exception ex) {
            throw new IllegalArgumentException("Number of jobs must be provided.", ex);
        }
        
        int nodeCount;
        try {
            nodeCount = Integer.parseInt(args[2]);
        } catch(Exception ex) {
            throw new IllegalArgumentException("Number of nodes must be provided.", ex);
        }
        
        new GenerateApp().runGenerated(args[0], jobCount, nodeCount);
    }
       
    private void runGenerated(String filename, int jobs, int machines) {
        InputGenerator generator = new InputGenerator(io);
        
        generator.setMaxMachinesPerJobPercentage(30);
        generator.setMaxTimeDiffHours(72);
        
        generator.generateAndSave(filename, jobs, machines);
    }   
}
