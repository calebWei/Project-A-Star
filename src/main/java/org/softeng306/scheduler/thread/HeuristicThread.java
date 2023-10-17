package org.softeng306.scheduler.thread;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.softeng306.graph.Task;
import org.softeng306.scheduler.ScheduleTreeNode;
import org.softeng306.scheduler.heuristics.HeuristicsValue;

import java.util.*;
import java.util.concurrent.Callable;

public class HeuristicThread implements Callable<List<ScheduleTreeNode>> {

    private final PriorityQueue<ScheduleTreeNode> openQueue;

    private final Set<ScheduleTreeNode> closedSet;

    private final List<ScheduleTreeNode> children;

    private final Graph<Task, DefaultWeightedEdge> inputGraph;

    private final int numProcessors;

    private final ScheduleTreeNode scheduleCurrent;

    private final List<ScheduleTreeNode> readyChildren = new ArrayList<>();

    public HeuristicThread(PriorityQueue<ScheduleTreeNode> openQueue, Set<ScheduleTreeNode> closedSet, List<ScheduleTreeNode> children,
    Graph<Task, DefaultWeightedEdge> inputGraph, int numProcessors, ScheduleTreeNode scheduleCurrent) {
        this.openQueue = openQueue;
        this.closedSet = closedSet;
        this.children = children;
        this.inputGraph = inputGraph;
        this.numProcessors = numProcessors;
        this.scheduleCurrent = scheduleCurrent;
    }

    @Override
    public List<ScheduleTreeNode> call() {
        for (ScheduleTreeNode child : children) {
            if (!openQueue.contains(child) && !closedSet.contains(child)) {
                child.setHeuristicValue(HeuristicsValue.getHeuristicsValue(inputGraph, numProcessors, child, scheduleCurrent));
                readyChildren.add(child);
            }
        }
        return readyChildren;
    }
}
