package org.softeng306.scheduler.heuristics;

import org.softeng306.graph.Task;
import org.softeng306.scheduler.ScheduleTreeNode;

import java.util.List;

public class ComputationBottomLevel {

    /**
     * Private constructor to prevent instantiation
     */
    private ComputationBottomLevel() {}


    /**
     * This calculates the Fblw of a state in the state space incrementally. Thus, O(1) when calculating fblw for a child state from a parent state.
     * @param currentNode current node
     * @param parentNode parent node
     * @return double representing the fblw value
     */
    public static double computeMaxBottomLevelSchedule(ScheduleTreeNode currentNode, ScheduleTreeNode parentNode) {
        Task lastTaskAdded = currentNode.getSchedule().getLastTaskAdded();
        double startAndBlw = (double) computeMaxBottomLevel(lastTaskAdded) + lastTaskAdded.getStartTime();
        currentNode.setFblw(startAndBlw);
        return Math.max(parentNode.getFblw(), startAndBlw);
    }

    /**
     * This method takes a Task (node) and it's associated task graph and calculates its computation bottom level.
     * A bottom level of a node is the sum of its run time (weight) plus the maximum path (node weight only) to an exit node.
     * Note: Ideally this should be calculated incrementally for the last node in each new state (ScheduleTreeNode) so that calculation of
     * fblw(schedule) is O(1).
     * @param newTask the task we want to assign date ready time
     * @return computation bottom level
     */
    public static int computeMaxBottomLevel(Task newTask) {
        List<Task> childrenNodes = newTask.getSuccessors();
        if (childrenNodes.isEmpty()) {
            //base case: if the node has no children, its bottom level is its weight (task duration)
            return newTask.getWeight();
        }

        int bottomLevel = 0;

        // recursively calculate the bottom level for each child node.
        for (Task child : childrenNodes) {
            int childBottomLevel = computeMaxBottomLevel(child);
            // update the bottom level if the child's bottom level is greater.
            if (childBottomLevel > bottomLevel) {
                bottomLevel = childBottomLevel;
            }
        }

        return newTask.getWeight() + bottomLevel;
    }
}
