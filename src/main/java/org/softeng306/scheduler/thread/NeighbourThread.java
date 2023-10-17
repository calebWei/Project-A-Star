package org.softeng306.scheduler.thread;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.softeng306.graph.Task;
import org.softeng306.scheduler.ISchedule;
import org.softeng306.scheduler.Schedule;
import org.softeng306.scheduler.ScheduleTreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class NeighbourThread implements Callable<List<ScheduleTreeNode>> {

    private final List<ScheduleTreeNode> neighbours = new ArrayList<>();

    private final ISchedule schedule;

    private final List<Task> nextTasks;

    private final int numProcessors;

    private final Graph<Task, DefaultWeightedEdge> inputGraph;

    public NeighbourThread(ISchedule schedule, List<Task> freeTasks, int numProcessors, Graph<Task, DefaultWeightedEdge> inputGraph) {
        this.schedule = schedule;
        this.nextTasks = freeTasks;
        this.numProcessors = numProcessors;
        this.inputGraph = inputGraph;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public List<ScheduleTreeNode> call() throws Exception {
        for (Task nextTask : nextTasks) {
            for (int procID = 0; procID < numProcessors; procID++) {
                int startTime = schedule.getProcFreeTime(procID);
                List<Task> prerequisites = nextTask.getPrerequisites();
                // Check if context switch occurred between next task and each of its prerequisite, and find the
                // latest data ready time for next task to start.
                int dataReadyTime;
                List<Integer> dataReadyTimes = new ArrayList<>();
                for (Task prerequisite : prerequisites) {
                    if (schedule.getScheduledProcNumber(prerequisite) == procID) {
                        dataReadyTime = schedule.getScheduledFinishTime(prerequisite);
                    } else {
                        dataReadyTime = (int) Math.round(schedule.getScheduledFinishTime(prerequisite) + inputGraph.getEdgeWeight(inputGraph.getEdge(prerequisite, nextTask)));
                    }
                    dataReadyTimes.add(dataReadyTime);
                }
                // If no prerequisites
                int latestDataReadyTime;
                if (prerequisites.isEmpty()) {
                    latestDataReadyTime = startTime;
                } else {
                    latestDataReadyTime = Collections.max(dataReadyTimes);
                }
                // If latest DataReadyTime is later than the start time of the current processor, update the new start time
                if (latestDataReadyTime > startTime) {
                    startTime = latestDataReadyTime;
                }

                // Assign the next task to the schedule
                ISchedule newSchedule = new Schedule(schedule);
                ScheduleTreeNode scheduleCopy = new ScheduleTreeNode(newSchedule);
                Task newTask = new Task(nextTask.getName(), nextTask.getWeight());
                newTask.setStartTime(startTime);
                newTask.setProcessorNumber(procID);
                scheduleCopy.getSchedule().scheduleTask(newTask);

                // Add schedule to returns list
                neighbours.add(scheduleCopy);
            }
        }

        return neighbours;
    }
}
