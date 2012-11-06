package org.jboss.qa.brms.hqp.rest;

import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.jboss.qa.brms.hqp.HudsonQueueSolver;
import org.jboss.qa.brms.hqp.HudsonQueueSolverImpl;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;
import org.jboss.qa.brms.hqp.rest.json.HudsonQueueJsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Planning REST API class.
 * Available on URL: http://[server]:[port]/rest/hudsonQueue
 * @author rsynek
 */
@Path("/hudsonQueue")
public class HudsonQueueResource {
    
    Logger logger = LoggerFactory.getLogger(HudsonQueueResource.class);
    
    private static final HudsonQueueSolver hudsonSolver = new HudsonQueueSolverImpl();
    
    private static final HudsonQueueJsonHelper jsonHelper = new HudsonQueueJsonHelper();
    
    private static final String ID_PROPERTY = "hqp.identification";
    
    /**
     * Info about the application.
     */
    @GET
    @Path("/info")
    @Produces(MediaType.TEXT_PLAIN)
    public String info(@Context HttpServletRequest request) { 
        String url = request.getRequestURL().toString();
        String info;
        Properties props = new Properties();
        try {
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("/hqp.properties"));
            info = props.getProperty(ID_PROPERTY);
            if(info == null) {
                info = "No identification found, specify hqp.identification property in WEB-INF/classes/hqp.properties.";
            }
        } catch (Exception ex) {
            info = "Cannot find WEB-INF/classes/hqp.properties.";
            logger.error(info, ex);
        }
        return info + " on URL " + url;
    }
    
    /**
     * Returns the difference between number of unassigned jobs (old solution - new solution).
     * 
     * -> higher number is better; it tells how much more jobs have been assigned
     */
    @GET
    @Path("/score")
    @Produces(MediaType.APPLICATION_JSON)
    public String getScore() {       
        return "{score: " + hudsonSolver.getRatio() + "}";
    }
    
    /**
     * Returns best available solution. Does not terminate solving.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSolution() {       
        return jsonHelper.getJson(hudsonSolver.getSolution());
    }
 
    /**
     * Starts solving the given queue.
     * @param queue list of jobs with their assignable nodes.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void startSolving(HudsonQueue queue) {   
        hudsonSolver.start(queue);
    }
    
    /**
     * Actualises the queue. New jobs are merged with previous.
     * @param queue the new queue to be solved
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void update(HudsonQueue queue) {
        hudsonSolver.update(queue);
    }
    
    /**
     * Stops the solver no matter what.
     */
    @DELETE
    public void stop() {
        hudsonSolver.stop();
    }
}
