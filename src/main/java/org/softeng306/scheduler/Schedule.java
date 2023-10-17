package org.softeng306.scheduler;

import org.softeng306.graph.Task;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Schedule can be empty to partial to full
 */
public class Schedule implements ISchedule {

    private final Set<Task> scheduledTasks;
    private final List<Task> unscheduledTasks;
    private final List<Integer> procFreeTime;
    private Task lastTaskAdded;
    public Schedule(List<Task> tasks, int numProcs){
        scheduledTasks = new HashSet<>();
        unscheduledTasks = tasks;
        procFreeTime = new CopyOnWriteArrayList<>(Collections.nCopies(numProcs, 0));
    }

    public Schedule(ISchedule schedule) {
        scheduledTasks = new HashSet<>(schedule.getScheduledTasks());
        unscheduledTasks = new ArrayList<>(schedule.getUnscheduledTasks());
        procFreeTime = new CopyOnWriteArrayList<>(schedule.getProcFreeTime());
    }

    @Override
    public void scheduleTask(Task task){
        scheduledTasks.add(task);
        unscheduledTasks.remove(task);
        lastTaskAdded = task;
        // Update corresponding processor's free time
        if (task.getFinishTime() > procFreeTime.get(task.getProcessorNumber())) {
            this.setProcFreeTime(task.getProcessorNumber(), task.getFinishTime());
        }
    }

    @Override
    public Set<Task> getScheduledTasks(){
        return scheduledTasks;
    }

    @Override
    public List<Task> getUnscheduledTasks(){
        return unscheduledTasks;
    }
    @Override
    public Task getLastTaskAdded() {
        return lastTaskAdded;
    }
    @Override
    public List<Task> getNextTasks() {
        List<Task> nextTasks = new ArrayList<>();
        for (Task task : unscheduledTasks) {
            List<Task> prerequisites = task.getPrerequisites();
            if (prerequisites == null || scheduledTasks.containsAll(prerequisites)) {
                nextTasks.add(task);
            }
        }
        return nextTasks;
    }

    @Override
    public boolean isComplete(){
        return unscheduledTasks.isEmpty();
    }

    @Override
    public int getMakeSpan() {
        return Collections.max(procFreeTime);
    }

    @Override
    public int getNumProcessors() {
        return procFreeTime.size();
    }

    @Override
    public List<Integer> getProcFreeTime() {
        return procFreeTime;
    }

    @Override
    public int getProcFreeTime(int proc) {
        return procFreeTime.get(proc);
    }

    @Override
    public void setProcFreeTime(int proc, int time){
        procFreeTime.set(proc, time);
    }

    @Override
    public int getScheduledFinishTime(Task task){
        for(Task t : scheduledTasks){
            if(task.equals(t)){
                return t.getFinishTime();
            }
        }
        return -1;
    }

    @Override
    public int getScheduledProcNumber(Task task){
        for(Task t : scheduledTasks){
            if(task.equals(t)){
                return t.getProcessorNumber();
            }
        }
        return -1;
    }

    @Override
    public boolean equals(Object other) {

        if(other == this){
            return true;
        }

        if (!(other instanceof Schedule otherSchedule)){
            return false;
        }

        // Check if same tasks scheduled in both schedules,
        // and that scheduled tasks of the same name all have the same start times.
        Map<String, Integer> startTimes = new HashMap<>();

        if(scheduledTasks.size() != otherSchedule.getScheduledTasks().size()){
            return false;
        }

        for (Task t : scheduledTasks){
            startTimes.put(t.getName(), t.getStartTime());
        }

        for (Task t : otherSchedule.getScheduledTasks()){
            if(startTimes.containsKey(t.getName())){
                if(startTimes.get(t.getName()) != t.getStartTime()){
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        for (Task t : scheduledTasks){
            hash *= t.getStartTime() * t.getName().hashCode();
        }
        return hash;
    }

}
