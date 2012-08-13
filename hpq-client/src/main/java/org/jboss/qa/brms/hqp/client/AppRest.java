/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.qa.brms.hqp.client;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 * Basic example of usage. 
 * Call startSolving() to initialise and run the solver. When you need the best available solution, just get it by calling 
 * getSolution(). When some changes in the job queue occur during computation, call update().
 * @author rsynek
 */
public class AppRest {
    
    private final static String URL = "http://localhost:8080/hqp-rest-1.0-SNAPSHOT/rest/";
    
    public static void main(String [] args) {
        new AppRest().run();
    }
    
    public void run() {
        final String file = "/org/jboss/qa/brms/hqp/sample_data_1.json";
        String json = null;
        try {
            json = FileUtils.readFileToString(new File(AppRest.class.getResource(file).getFile()), "UTF-8");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        RestClientHelper.getClient(URL).startSolving(json);
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        
        String solution = RestClientHelper.getClient(URL).getSolution();
               
        System.out.println(solution);
        
        System.out.println(RestClientHelper.getClient(URL).getScore());
    }
}
