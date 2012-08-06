/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.qa.brms.hqp.io;

import java.io.File;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;

/**
 * JSON parsing and output.
 * @author rsynek
 */
public class JsonDataIO {
    
    private ObjectMapper mapper;
    
    public HudsonQueue parseJsonString(String json) {
        try {
            return getObjectMapper().readValue(json, HudsonQueue.class);
        } catch (IOException ex) {
            throw new RuntimeException(String.format("String \"%s\" cannot be parsed.", json), ex);
        } 
    }
    
    public HudsonQueue parseJsonFile(File f) {
        try {
            return getObjectMapper().readValue(f, HudsonQueue.class);
        } catch (IOException ex) {
            throw new RuntimeException(String.format("File %s cannot be parsed.", f.getName()), ex);
        }
    }
    
    public String getJson(HudsonQueue queue) {
        try {
            return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(queue);
        } catch (IOException ex) {
            throw new RuntimeException("Solution cannot be converted do JSON.", ex);
        } 
    }
    
    private ObjectMapper getObjectMapper() {
        if(mapper == null) {
            mapper = new ObjectMapper();
        }
        return mapper;
    }
}
