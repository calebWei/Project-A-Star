package org.softeng306.scheduler.heuristics;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.softeng306.graph.Task;
import org.softeng306.scheduler.ScheduleTreeNode;

public class HeuristicsValue{

    /**
     * Private constructor to prevent instantiation
     */
    private HeuristicsValue() { }

    /**
     * Set the heuristic value to a schedule
     *
     * @param inputGraph representing all jobs
     * @param numProcessors number of processors
     * @param currentNode the current partial schedule we are at from the decision tree
     * @return heuristic value a.k.a f(s)
     */
    public static double getHeuristicsValue(Graph<Task, DefaultWeightedEdge> inputGraph, int numProcessors, ScheduleTreeNode currentNode, ScheduleTreeNode parentNode) {
        double  fbl = ComputationBottomLevel.computeMaxBottomLevelSchedule(currentNode, parentNode);
        double idleTime = IdleTime.getIdleTime(numProcessors,currentNode);
        double dataReadyTime = DataReadyTime.getDRT(inputGraph,numProcessors,currentNode);
        return Math.max(fbl, Math.max(idleTime, dataReadyTime));
    }
}
