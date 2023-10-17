package org.softeng306.scheduler;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.softeng306.graph.Task;
import org.softeng306.scheduler.thread.NeighbourThread;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * ScheduleTreeNode represents a node for searching possible schedule combinations in a solution tree.
 * It contains an object implementing Ischedule to represent the partial schedule state at that node,
 * as well as a heuristic value for the node itself for search purposes.
 */
public class ScheduleTreeNode implements Comparable<ScheduleTreeNode> {
    private double hValue = -1;
    private final ISchedule schedule;
    private double fblw = 0;


    public ScheduleTreeNode(ISchedule schedule) {
        this.schedule = schedule;
    }

    public ISchedule getSchedule() {
        return schedule;
    }

    public double getHeuristicValue() {
        return hValue;
    }

    public void setHeuristicValue(double hValue) {
        this.hValue = hValue;
    }
    public double getFblw() {
        return fblw;
    }

    public void setFblw(double fblw) {
        this.fblw = fblw;
    }

    /**
     * Given a schedule and a list of possible next tasks, generate a list of all possible schedules created
     * by scheduling one task from the free task list to the current schedule.
     * @param inputGraph representing all jobs
     * @param numProcessors number of processors
     * @return A list of different schedules, where each schedule is a possibility of scheduling one of the available
     * tasks to the current schedule.
     */
    public List<ScheduleTreeNode> getNeighbours(Graph<Task, DefaultWeightedEdge> inputGraph, int numProcessors) {
        List<ScheduleTreeNode> neighbours = new ArrayList<>();
        List<Task> freeTasks = schedule.getNextTasks();

        // pruning : fixed task ordering if applicable for the current state
        List<Task> sortedFtoList = pruningFTO(freeTasks, inputGraph);
        if (sortedFtoList != null) {
            freeTasks.clear();
            //choose a single task from the pruned free(s) list to reduce the branching factor to |P|.
            freeTasks.add(sortedFtoList.get(0));
            //continue getNeighbours() as normal...
        }

        for (Task nextTask : freeTasks) {
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
                // No prerequisites mean data ready time is the earliest possible (anytime)
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

    /**
     * Given a schedule and a list of possible next tasks, generate a list of all possible schedule by scheduling
     * one task to the current schedule. Multithreaded version of getNeighbours().
     * @param inputGraph representing all jobs
     * @param numProcessors number of processors
     * @param numCores number of cores (threads)
     * @return A list of different schedules, where each schedule is a possibility of scheduling one of the available
     * tasks to the current schedule.
     */
    public List<ScheduleTreeNode> getNeighboursParallel(Graph<Task, DefaultWeightedEdge> inputGraph, int numProcessors, int numCores, ExecutorService executor) {
        List<ScheduleTreeNode> neighbours = new ArrayList<>();
        List<Task> freeTasks = schedule.getNextTasks();

        // pruning : fixed task ordering if applicable for the current state
        List<Task> sortedFtoList = pruningFTO(freeTasks, inputGraph);
        if (sortedFtoList != null) {
            freeTasks.clear();
            //choose a single task from the pruned free(s) list to reduce the branching factor to |P|.
            freeTasks.add(sortedFtoList.get(0));
            //continue getNeighbours() as normal...
        }

        int chunkSize = (int) Math.ceil((double)freeTasks.size() / numCores);
        List<Future<List<ScheduleTreeNode>>> futures = new ArrayList<>();
        for (int i = 0; i < numCores; i++) {
            int start = i * chunkSize;
            if (start >= freeTasks.size()) {
                break;
            }
            int end = Math.min(start + chunkSize, freeTasks.size());
            List<Task> chunkFreeTasks = new ArrayList<>(freeTasks.subList(start, end));
            NeighbourThread neighbourCallable = new NeighbourThread(schedule, chunkFreeTasks, numProcessors, inputGraph);

            futures.add(executor.submit(neighbourCallable)); // submit returns a Future
        }

        for (Future<List<ScheduleTreeNode>> future : futures) {
            try {
                neighbours.addAll(future.get()); // retrieve the result of the computation
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        return neighbours;
    }

    /**
     * Applies fixed task ordering sorting to a list of tasks according to FTOTaskComparator.
     * @param freeTasks free tasks for the given partial schedule.
     * @param inputGraph graph of tasks.
     * @return tasks in fixed task ordering order.
     */
    private List<Task> pruningFTO(List<Task> freeTasks, Graph<Task, DefaultWeightedEdge> inputGraph) {
        // dont do task sorting if condition 1 fails
        if (!isFixingOrderCondition(freeTasks)) {
            return null;
        }
        // because the FixingOrderCondition confirmed the tasks in free(s) has only at most 1 parent and child,
        // we only need to consider 1 parent for data ready time here
        List<Task> sortedFreeTasks = new ArrayList<>(freeTasks);
        sortedFreeTasks.sort(new FTOTaskComparator(inputGraph));
        return sortedFreeTasks;
    }

    /**
     * Assesses fixed task ordering conditions, a method for pruning states from the solution tree.
     * @param freeTasks Tasks able to be added to the current schedule (not scheduled + prerequisites scheduled)
     * @return True if fixed task ordering should be performed given the free tasks of the current partial schedule.
     */
    private boolean isFixingOrderCondition(List<Task> freeTasks) {
        Set<Task> childrenUnion = new HashSet<>();
        int sameProcessorNumber = -1;
        boolean isFirstParent = true;
        for (Task nf : freeTasks) {
            // Condition 1: ensure nf has at most one parent and at most one child
            if (!(nf.getPrerequisites().size() <= 1 && nf.getSuccessors().size() <= 1)) {
                return false;
            }
            // Condition 2: nf has a child, |childs(nf)| = 1, then it is the same child as for any other task ng in free(s):
            childrenUnion.addAll(nf.getSuccessors());
            // Condition 3: if nf has a parent, |parents(nf)| = 1, then all other parents of tasks ng in free(s) are allocated to the same processor P
            for (Task nfParent : nf.getPrerequisites()) {
                if (isFirstParent) {
                    //initialise the base case first
                    sameProcessorNumber = nfParent.getProcessorNumber();
                    isFirstParent = false;
                } else if (nfParent.getProcessorNumber() != sameProcessorNumber) {
                    return false;
                }
            }
        }
        // Condition 2 cont: The union of all tasks' children in free(s) should be 1
        // All conditions have passed if this is true
        return childrenUnion.size() <= 1;
    }

    /**
     * Equality (equivalence) for ScheduleTreeNodes is determined only by their schedules.
     * I.e. two nodes containing equivalent schedules are deemed equivalent.
     * @param other the other object to be compared.
     * @return True if nodes are equivalent.
     */
    @Override
    public boolean equals(Object other){
        if(other == this){
            return true;
        }

        if(!(other instanceof ScheduleTreeNode otherScheduleTreeNode)){
            return false;
        }

        return this.getSchedule().equals(otherScheduleTreeNode.getSchedule());
    }

    /**
     * ScheduleTreeNodes are compared and ordered by their heuristic values.
     * @param other the object to be compared.
     * @return 0 if heuristics are equal, negative integer if this heuristic is smaller than that, and positive integer if heuristic is larger than that.
     */
    @Override
    public int compareTo(ScheduleTreeNode other) {
        return Double.compare(this.getHeuristicValue(), other.getHeuristicValue());
    }

    @Override
    public int hashCode(){
        return schedule.hashCode();
    }
}
