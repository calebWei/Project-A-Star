package org.softeng306.scheduler;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.softeng306.graph.Task;
import org.softeng306.scheduler.thread.HeuristicThread;

import java.util.*;
import java.util.concurrent.*;

public class ParallelScheduler implements Scheduler {

    private final PriorityQueue<ScheduleTreeNode> openQueue = new PriorityQueue<>();

    private final Set<ScheduleTreeNode> closedSet = new HashSet<>();

    private final List<ScheduleTreeNode> orderedClosedSet = new ArrayList<>();
    private int numStatesExamined;
    private ScheduleTreeNode scheduleCurrent;

    /**
     * Performs a multithreaded A* search for an optimal schedule.
     * @param inputGraph task graph
     * @param numProcessors number of available processors
     * @param numCores number of cores (threads) available
     * @return full valid schedule
     */
    @Override
    public ISchedule search(Graph<Task, DefaultWeightedEdge> inputGraph, int numProcessors, int numCores) {

        ExecutorService executor = Executors.newFixedThreadPool(numCores);
        // Initialize empty schedule
        ISchedule newSchedule = new Schedule(inputGraph.vertexSet().stream().toList(), numProcessors);
        scheduleCurrent = new ScheduleTreeNode(newSchedule);

        // Create a fixed thread pool
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
                executor.shutdown(); // shut down the executor
                return scheduleCurrent.getSchedule();
            }
            // Get children nodes of s_current, remove duplicates defined by ScheduleTreeNode's
            List<ScheduleTreeNode> children = scheduleCurrent.getNeighboursParallel(inputGraph, numProcessors, numCores, executor).stream().distinct().toList();

            // If any of the children is in closed or open lists, discard the child, otherwise add to open list. Task
            // split across multiple threads.
            int chunkSize = (int) Math.ceil((double)children.size() / numCores);
            List<Future<List<ScheduleTreeNode>>> futures = new ArrayList<>();
            for (int i = 0; i < numCores; i++) {
                int start = i * chunkSize;
                if (start >= children.size()) {
                    break;
                }
                int end = Math.min(start + chunkSize, children.size());
                List<ScheduleTreeNode> chunkChildren = new ArrayList<>(children.subList(start, end));
                HeuristicThread heuristicCallable = new HeuristicThread(openQueue, closedSet, chunkChildren, inputGraph, numProcessors, scheduleCurrent);

                futures.add(executor.submit(heuristicCallable)); // submit returns a Future
            }

            for (Future<List<ScheduleTreeNode>> future : futures) {
                try {
                    openQueue.addAll(future.get()); // retrieve the result of the computation
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }

            // Move s_current to closed list
            closedSet.add(scheduleCurrent);
        }
        executor.shutdown(); // shut down the executor

        return new Schedule(new ArrayList<>(), 0);
    }

    @Override
    public List<ScheduleTreeNode> getOrderedClosedSet() {
        return orderedClosedSet;
    }

    @Override
    public int getNumStatesExamined() {
        return numStatesExamined;
    }

    @Override
    public ScheduleTreeNode getScheduleCurrent() {
        return scheduleCurrent;
    }


}
