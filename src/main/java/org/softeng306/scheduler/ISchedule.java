package org.softeng306.scheduler;

import org.softeng306.graph.Task;

import java.util.List;
import java.util.Set;

public interface ISchedule {
    Set<Task> getScheduledTasks();

    List<Task> getUnscheduledTasks();

    Task getLastTaskAdded();

    void setProcFreeTime(int proc, int time);

    /**
     * Add a task to the schedule.
     * @param task the task to add to the schedule.
     */
    void scheduleTask(Task task);

    /**
     * @return list of tasks that have not been scheduled, and have all of their respective prerequisite tasks scheduled.
     */
    List<Task> getNextTasks();

    /**
     * @return true if the schedule has no unscheduled tasks left. I.e. all tasks have been scheduled.
     */
    boolean isComplete();

    /**
     * @return the total length of the schedule, i.e. the latest scheduled task's finish time.
     */
    int getMakeSpan();

    int getNumProcessors();

    /**
     * @return list of the earliest next times that each processor (index processor number) are each able to perform tasks.
     */
    List<Integer> getProcFreeTime();

    /**
     * @param proc the processor number of the processor to check the free time of.
     * @return The earliest next time that the given processor is able to perform tasks.
     */
    int getProcFreeTime(int proc);

    /**
     * Calculates and returns the finish time of the given task in this schedule.
     * @param task the task to check the finish time of.
     * @return the finish time of the task in this schedule.
     */
    int getScheduledFinishTime(Task task);

    /**
     * Returns the processor a task is scheduled on in this schedule.
     * @param task the task to check the schedule for its processor number.
     * @return the processor number of the task if it is scheduled, or -1 if it is not scheduled.
     */
    int getScheduledProcNumber(Task task);

    @Override
    boolean equals(Object other);
}