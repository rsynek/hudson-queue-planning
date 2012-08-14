package org.jboss.qa.brms.hqp.io;

import java.io.File;
import org.drools.planner.benchmark.api.ProblemIO;
import org.drools.planner.core.solution.Solution;
import org.jboss.qa.brms.hqp.domain.HudsonQueue;

/**
 * Adapter for reading/writing benchmark datasets.
 * @author rsynek
 */
public class JsonProblemIO extends JsonFileSerializer implements ProblemIO {
    private static final String EXT = "json";
    
    @Override
    public String getFileExtension() {
        return EXT;
    }

    /**
     * Read solution from input file. Call initialization method.
     * @param inputSolutionFile input json file with hudson queue model.
     * @return solution
     */
    @Override
    public Solution read(File inputSolutionFile) {
        HudsonQueue queue = readJson(inputSolutionFile);
        queue.initSolution();
        return queue;
    }

    /**
     * Serializes the solution into json file.
     * @param solution Solution instance.
     * @param outputSolutionFile output json file.
     */
    @Override
    public void write(Solution solution, File outputSolutionFile) {
        writeJson((HudsonQueue)solution, outputSolutionFile);
    }
}
