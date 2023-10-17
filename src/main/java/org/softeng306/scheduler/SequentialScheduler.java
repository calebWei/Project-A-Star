package org.softeng306.scheduler;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.softeng306.graph.Task;
import org.softeng306.scheduler.heuristics.HeuristicsValue;

import java.util.*;

public class SequentialScheduler implements Scheduler {
    private final PriorityQueue<ScheduleTreeNode> openQueue = new PriorityQueue<>();
    private final Set<ScheduleTreeNode> closedSet = new HashSet<>();
    private final List<ScheduleTreeNode> orderedClosedSet = new ArrayList<>();

    private int numStatesExamined;
    private ScheduleTreeNode scheduleCurrent;

    /**
     * Performs a sequential A* search for an optimal schedule.
     * @param inputGraph task graph
     * @param numProcessors number of available processors
     * @param numCores can be any number, parameter not used
     * @return full valid schedule
     */
    @Override
    public ISchedule search(Graph<Task, DefaultWeightedEdge> inputGraph, int numProcessors, int numCores) {
        // Initialize empty schedule
        ISchedule newSchedule = new Schedule(inputGraph.vertexSet().stream().toList(), numProcessors);
        scheduleCurrent = new ScheduleTreeNode(newSchedule);

        // Put s_init in the open list
        scheduleCurrent.setHeuristicValue(0);
        openQueue.add(scheduleCurrent);

        while (!openQueue.isEmpty()) {
            // Remove s_current from open list the schedule with the best f(n)
            scheduleCurrent = openQueue.poll();
            numStatesExamined++;
            orderedClosedSet.add(scheduleCurrent);
            // Check if s_current is a full schedule
            if (scheduleCurrent.getSchedule().isComplete()) {
                return scheduleCurrent.getSchedule();
            }
            // Get children nodes of s_current, remove duplicates defined by ScheduleTreeNode's
            List<ScheduleTreeNode> children = scheduleCurrent.getNeighbours(inputGraph, numProcessors).stream().distinct().toList();
            // If any of the children is in closed or open lists, discard the child, otherwise add to open list
            for (ScheduleTreeNode child : children) {
                if (!openQueue.contains(child) && !closedSet.contains(child)) {
                    child.setHeuristicValue(HeuristicsValue.getHeuristicsValue(inputGraph, numProcessors, child, scheduleCurrent));
                    openQueue.add(child);
                }
            }
            // Move s_current to closed list
            closedSet.add(scheduleCurrent);

        }
        return new Schedule(new ArrayList<>(), 0);
    }

    @Override
    public List<ScheduleTreeNode> getOrderedClosedSet() {
        return orderedClosedSet;
    }

    @Override
    public int getNumStatesExamined() { return numStatesExamined;}

    @Override
    public ScheduleTreeNode getScheduleCurrent() {
        return scheduleCurrent;
    }


}
