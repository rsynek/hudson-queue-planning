/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.qa.brms.hqp.client;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Planning REST API class.
 * Available on URL: http://[server]:[port]/rest/hudsonQueue
 * @author rsynek
 */
@Path("/hudsonQueue")
public interface HudsonQueueResource {
    
    /**
     * Returns the difference between number of unassigned jobs (old solution - new solution).
     * -> higher number is better; it tells how much more jobs have been assigned
     * 
     * format: {score: 4}
     */
    @GET
    @Path("/score")
    @Produces(MediaType.APPLICATION_JSON)
    public String getScore();
    
    /**
     * Returns best available solution. Does not terminate solving.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSolution();
 
    /**
     * Starts solving the given queue.
     * @param queue list of jobs with their assignable nodes.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void startSolving(String problem);
    
    /**
     * Actualises the queue. New jobs are merged with previous.
     * @param queue the new queue to be solved
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void update(String problem);
    
    /**
     * Stops solver no matter what.
     */
    @DELETE
    public void stop();
}
