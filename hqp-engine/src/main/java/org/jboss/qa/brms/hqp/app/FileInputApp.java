package org.jboss.qa.brms.hqp.app;

import java.io.File;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;

/**
 * Run solver with input queue from provided file.
 * usage: java org.jboss.qa.brms.hqp.app.FileInputApp filename timeout
 * @author rsynek
 */
public class FileInputApp extends AbstractApplication {
    
    public static void main(String [] args) {
        if(args[0] == null) {
            throw new IllegalArgumentException("Filename with input in JSON format must be provided.");
        }
        
        long timeout;
        try {
            timeout = Long.parseLong(args[1]);
        } catch(Exception ex) {
            throw new IllegalArgumentException("Timeout [msec] must be provided.", ex);
        }
        
        if(args.length == 3 && args[2].equalsIgnoreCase("-c")) {
            new FileInputApp().runContinually(args[0], timeout);
        } else {
            new FileInputApp().run(args[0], timeout);
        }    
    }
    
    private HudsonQueue getFromJsonFile(String file) {
        return io.readJson(new File(file));
    }
    
    private void runContinually(String filename, long waiting) {
        runContinually(getFromJsonFile(filename), waiting);
    }
    
    private void run(String filename, long timeout) {
        HudsonQueue queue = getFromJsonFile(filename);
        run(queue, timeout);
    }
}
