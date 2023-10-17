package org.softeng306.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A task that could be scheduled in a schedule by defining its starting time and corresponding processor
 */
public class Task extends Node implements Comparable<Task>{

    private int startTime = -1;

    private int procNumber;

    private List<Task> prerequisites = new ArrayList<>();

    private List<Task> successors = new ArrayList<>();

    public Task (String nodeName, int computationCost) {
        super(nodeName, computationCost);
    }

    public void setStartTime(int startTime){
        this.startTime = startTime;
    }

    public int getStartTime(){
        return this.startTime;
    }

    public int getFinishTime() {
        return this.startTime + this.getWeight();
    }

    public void setProcessorNumber(int processorNumber){
        this.procNumber = processorNumber;
    }

    public int getProcessorNumber(){
        return this.procNumber;
    }

    public List<Task> getPrerequisites(){
        return this.prerequisites;
    }

    public void setPrerequisites(List<Task> prerequisites){
        this.prerequisites = prerequisites;
    }
    public List<Task> getSuccessors() {
        return successors;
    }

    public void setSuccessors(List<Task> successors) {
        this.successors = successors;
    }

    /**
     * @return the list of time steps which this task is active across.
     */
    public List<Integer> getPeriod(){
        List<Integer> period = new ArrayList<>();
        for (int i = startTime; i < this.startTime + this.getWeight(); i++){
            period.add(i);
        }
        return period;
    }

    @Override
    public boolean equals(Object other){
        if(other == this){
            return true;
        }

        if (!(other instanceof Task t)){
            return false;
        }

        return this.getName().equals(t.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    /**
     *
     * @param e the object to be compared.
     * @return -ive integer if this task starts before other task, +ve if this task starts after, or 0 if both tasks start at the same time.
     */
    @Override
    public int compareTo(Task e) {
        if (this.getStartTime() < e.getStartTime()) {
            return -1;
        } else if (this.getStartTime() > e.getStartTime()) {
            return 1;
        }
        return 0;
    }
}
