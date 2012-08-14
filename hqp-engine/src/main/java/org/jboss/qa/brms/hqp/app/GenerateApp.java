package org.jboss.qa.brms.hqp.app;

import org.jboss.qa.brms.hqp.io.generate.InputGenerator;

/**
 * Generates new input JSON file.
 * usage: java org.jboss.qa.brms.hqp.app.GenerateApp filename jobCount nodeCount percentage
 * 
 */
public class GenerateApp extends AbstractApplication {  
    
    public static void main(String [] args) {
        if(args[0] == null) {
            throw new IllegalArgumentException("Filename must be provided.");
        }
        
        int jobCount = parseInt(args[1], "Number of jobs must be provided.");
        int nodeCount = parseInt(args[2], "Number of nodes must be provided.");
        int percentage = parseInt(args[3],"Percentage of available nodes for each job must be provided.");
             
        new GenerateApp().runGenerated(args[0], jobCount, nodeCount, percentage);
    }
       
    private static int parseInt(String number, String msg) {
        try {
             return Integer.parseInt(number);
        } catch(Exception ex) {
            throw new IllegalArgumentException(msg, ex);
        }
    }
    
    private void runGenerated(String filename, int jobs, int machines, int percentage) {
        InputGenerator generator = new InputGenerator(io);
        
        generator.setMaxMachinesPerJobPercentage(percentage);
        generator.setMaxTimeDiffHours(72);
        
        generator.generateAndSave(filename, jobs, machines);
    }   
}
