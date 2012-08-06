/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.qa.brms.hqp.rest.json;

import java.io.IOException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;

/**
 * Serializing HudsonQueue solution to Json using custom JsonSerializer.
 * @author rsynek
 */
public class HudsonQueueJsonHelper {

    private ObjectMapper mapper;

    public HudsonQueueJsonHelper() {
        mapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1, 9, 8, null));
        simpleModule.addSerializer(new HudsonQueueJsonSerializer());
        mapper.registerModule(simpleModule);
    }

    public String getJson(HudsonQueue hq) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(hq);
        } catch (IOException ex) {
            throw new RuntimeException("Solution cannot be converted do JSON.", ex);
        }
    }
}
