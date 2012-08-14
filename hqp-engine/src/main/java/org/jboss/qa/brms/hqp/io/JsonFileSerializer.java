package org.jboss.qa.brms.hqp.io;

import java.io.File;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;

/**
 * JSON parsing and output.
 * @author rsynek
 */
public class JsonFileSerializer {
  
    private static ObjectMapper mapper;
  
    /**
     * Reads HudsonQueue instance from input json file.
     * @param f json file.
     * @return HudsonQueue instance.
     */
    public HudsonQueue readJson(File f) {
        try {
            return getMapper().readValue(f, HudsonQueue.class);
        } catch (IOException ex) {
            throw new RuntimeException(String.format("File %s cannot be parsed.", f.getName()), ex);
        }
    }
 
    public HudsonQueue readJson(String filename) {
        return readJson(new File(filename));
    }
    
    /**
     * Writes HudsonQueue instance into json file.
     * @param queue HudsonQueue instance.
     * @param f output json file.
     */
    public void writeJson(HudsonQueue queue, File f) {
        try {
            getMapper().writerWithDefaultPrettyPrinter().writeValue(f, queue);
        } catch (IOException ex) {
            throw new RuntimeException("Solution cannot be converted do JSON.", ex);
        } 
    }
    
    public void writeJson(HudsonQueue queue, String filename) {
        writeJson(queue, new File(filename));
    }
    
    private synchronized ObjectMapper getMapper() {
        if(mapper == null) {
            mapper = new ObjectMapper();
        }
        return mapper;
    }
}
