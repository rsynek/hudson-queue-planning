package org.jboss.qa.brms.hqp.rest.json;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;
import org.jboss.qa.brms.hqp.domain.Job;

/**
 * Custom serializer for simpler output.
 * data format:
 * {'solution': [{'id':124, 'name':'job@124', 'node':'vmg77-win2k3-x86_64'}]}
 *
 * @author rsynek
 */
public class HudsonQueueJsonSerializer extends JsonSerializer<HudsonQueue> {

    @Override
    public void serialize(HudsonQueue t, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
        jg.useDefaultPrettyPrinter();

        jg.writeStartObject();
        jg.writeFieldName("solution");
        jg.writeStartArray();
        for (Job j : t.getJobQueue()) {
            jg.writeStartObject();
            jg.writeNumberField("id", j.getId());
            jg.writeStringField("name", j.getName());
            jg.writeStringField("node", j.getAssigned().getMachine().getName());
            jg.writeEndObject();
        }
        jg.writeEndArray();
        jg.writeEndObject();
        jg.flush();
        jg.close();
    }

    @Override
    public Class<HudsonQueue> handledType() {
        return HudsonQueue.class;
    }
}
