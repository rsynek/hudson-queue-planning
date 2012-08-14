package org.jboss.qa.brms.hqp.app;

import org.drools.planner.benchmark.api.PlannerBenchmark;
import org.drools.planner.benchmark.config.XmlPlannerBenchmarkFactory;

/**
 * Simple benchmarking class.
 * @author rsynek
 */
public class BenchmarkApp {

    private static final String CONFIG = "/org/jboss/qa/brms/hqp/benchmark/hudsonQueuePlanningBenchmarkConfig.xml";
    
    public static void main(String[] args) {
        new BenchmarkApp().run();
    }
    
    private void run() {
        XmlPlannerBenchmarkFactory plannerBenchmarkFactory = new XmlPlannerBenchmarkFactory();
        plannerBenchmarkFactory.configure(CONFIG);
        
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();

        plannerBenchmark.benchmark();
    }
}
