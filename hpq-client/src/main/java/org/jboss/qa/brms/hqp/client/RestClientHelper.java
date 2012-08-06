package org.jboss.qa.brms.hqp.client;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class RestClientHelper {

    private static HudsonQueueResource client;
    
    private static void createClient(String url) {
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
        java.util.Map<String, Object> attrs = new java.util.HashMap<String, Object>();
        client = ProxyFactory.create(HudsonQueueResource.class, url, attrs);
    }
    
    public static HudsonQueueResource getClient(String url) {
        if(client == null) {
            createClient(url);
        }
        return client;
    }

}
