package org.softeng306.scheduler.heuristics;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.softeng306.graph.Task;
import org.softeng306.scheduler.ScheduleTreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataReadyTime {

    /**
     * Private constructor to prevent instantiation
     */
    private DataReadyTime() {}

    /**
     * Given a task to insert, find the earliest time for it to start across all processors.
     *
     * @param inputGraph representing all jobs
     * @param numProcessors number of processors
     * @param currentNode the current partial schedule we are at from the decision tree
     * @return int representing its start time
     */
    public static double getDRT(Graph<Task, DefaultWeightedEdge> inputGraph, int numProcessors, ScheduleTreeNode currentNode) {
        List<Integer> fDataReadyTimes = new ArrayList<>();
        List<Task> freeTasks = currentNode.getSchedule().getNextTasks();

        for(Task task : freeTasks) {
            List<Integer> procDRT = new ArrayList<>();
            List<Task> prerequisites = task.getPrerequisites();
            // Calculate the data-ready time of each "free" task (a task whose prerequisites have already been added to the schedule),
            // on each processor. Tasks with no prerequisites are included in this.
            for (int procID = 0; procID < numProcessors; procID++) {
                List<Integer> dataReadyTimes = new ArrayList<>();
                for (Task prerequisite : prerequisites) {
                    int procDataReadyTime = currentNode.getSchedule().getScheduledFinishTime(prerequisite);
                    if (prerequisite.getProcessorNumber() != procID) {
                        procDataReadyTime += inputGraph.getEdgeWeight(inputGraph.getEdge(prerequisite, task));
                    }
                    dataReadyTimes.add(procDataReadyTime);
                }
                if (dataReadyTimes.isEmpty()) {
                    procDRT.add(currentNode.getSchedule().getProcFreeTime(procID));
                } else {
                    dataReadyTimes.stream().max(Integer::compareTo).ifPresent(procDRT::add);
                }
            }
            // Record the minimum start time according to the data-ready time on each processor for this task.
            Optional<Integer> minTaskDataReadyTime = procDRT.stream().min(Integer::compareTo);

            if(minTaskDataReadyTime.isPresent()){
                int taskFDRT = minTaskDataReadyTime.get() + ComputationBottomLevel.computeMaxBottomLevel(task);
                fDataReadyTimes.add(taskFDRT);
            }
        }
        return fDataReadyTimes.stream().max(Integer::compareTo).orElse(-1);
    }
}
