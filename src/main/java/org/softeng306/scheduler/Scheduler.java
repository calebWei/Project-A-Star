package org.softeng306.scheduler;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.softeng306.graph.Task;

import java.util.List;

public interface Scheduler {
    /**
     * Performs a search for an optimal schedule on a graph of tasks.
     * @param inputGraph the graph of tasks with weights (compute times), with directed weighted edges from prerequisites to successors (transfer times).
     * @param numProcessors the number of processors for the schedule.
     * @param numCores the number of cores to run the algorithm on (implements a parallelised search).
     * @return an ISchedule object that represents the solution to the search, an optimal schedule.
     */
    ISchedule search(Graph<Task, DefaultWeightedEdge> inputGraph, int numProcessors, int numCores);

    /**
     * @return a list of the states (ScheduleTreeNodes) that will not be visited again in the search implementation.
     */
    List<ScheduleTreeNode> getOrderedClosedSet();

    /**
     * @return the number of states the implementation search algorithm visited to reach the solution.
     */
    int getNumStatesExamined();

    /**
     * @return the schedule that is currently being examined by the search implementation.
     */
    ScheduleTreeNode getScheduleCurrent();

}
