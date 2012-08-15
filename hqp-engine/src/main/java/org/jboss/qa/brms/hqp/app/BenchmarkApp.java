package org.jboss.qa.brms.hqp.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.planner.benchmark.api.PlannerBenchmark;
import org.drools.planner.benchmark.config.XmlPlannerBenchmarkFactory;

/**
 * Simple benchmarking class.
 * Uses free-marker template and provides parameters to create solvers.
 * @author rsynek
 */
public class BenchmarkApp {

    private static final String TEMPLATE = "/org/jboss/qa/brms/hqp/benchmark/benchmarkConfigTemplate.ftl";
      
    public static void main(String[] args) {
        new BenchmarkApp().run();
    }
    
    private void run() {
        XmlPlannerBenchmarkFactory plannerBenchmarkFactory = new XmlPlannerBenchmarkFactory();      
        plannerBenchmarkFactory.configureFromTemplate(BenchmarkApp.class.getResourceAsStream(TEMPLATE), getParams());
        
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        plannerBenchmark.benchmark();
    }
    
    private Map getParams() {
        final int [] hardScore = {2, 4, 8, 16, 32, 64};
        final int [] softScore = {10, 100, 1000};
        final int [] planningEntityTabuSize = {2, 4, 10, 20, 30};
        
        List<int[]> paramList = new ArrayList<int[]>();
        for(int hard : hardScore) {
            for(int soft : softScore) {
                for(int tabu : planningEntityTabuSize) {
                    paramList.add(new int [] {hard, soft, tabu});
                }
            }
        }
        
        Map params = new HashMap();
        params.put("params", paramList);
        return params;
    }
}
