package org.softeng306.scheduler.thread;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.softeng306.fileio.IO;
import org.softeng306.graph.Task;
import org.softeng306.scheduler.ISchedule;
import org.softeng306.scheduler.ScheduleTreeNode;
import org.softeng306.scheduler.Scheduler;
import org.softeng306.utility.ScheduleIOUtility;

import java.util.List;

public class SchedulerThread extends Thread{
    private final Scheduler scheduler;
    private final IO io;
    private final Graph<Task, DefaultWeightedEdge> inputGraph;
    private boolean isDone;

    public SchedulerThread(Scheduler scheduler, IO io, Graph<Task, DefaultWeightedEdge> inputGraph) {
        this.scheduler = scheduler;
        this.io = io;
        this.inputGraph = inputGraph;
        this.isDone = false;

    }

    @Override
    public void run() {
        ISchedule schedule = scheduler.search(inputGraph, io.getNumberOfProcessor(), io.getNumberOfCores());
        ScheduleIOUtility.exportSchedule(schedule, io.getOutputFilePath());
        isDone = true;
    }

    public boolean isDone() {
        return this.isDone;
    }
    public ScheduleTreeNode getCurrentSchedule() {
        return scheduler.getScheduleCurrent();
    }
    public int getNumberOfStatesExamined() {
        return this.scheduler.getNumStatesExamined();
    }
    public List<ScheduleTreeNode> getOrderedClosedSet() {
        return this.scheduler.getOrderedClosedSet();
    }

}
