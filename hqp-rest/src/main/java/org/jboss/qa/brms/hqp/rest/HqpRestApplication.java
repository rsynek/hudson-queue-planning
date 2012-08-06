/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.qa.brms.hqp.rest;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * REST API root class.
 * Resources are available on URL: http://[server]:[port]/rest/[resource]
 * @author rsynek
 */
@ApplicationPath("/rest")
public class HqpRestApplication extends Application {

    private Set<Object> singletons = new HashSet<Object>();

    public HqpRestApplication() {
        singletons.add(new HudsonQueueResource());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
