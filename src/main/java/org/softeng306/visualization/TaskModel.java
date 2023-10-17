package org.softeng306.visualization;

/**
 * The ScheduleRow class is the rows in task table
 */
public class TaskModel {
    private String node;
    private final int startTime;
    private final int procNumber;

    public TaskModel(String node, int startTime, int procNumber){
        this.node = node;
        this.startTime = startTime;
        this.procNumber = procNumber;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getProcNumber() {
        return procNumber;
    }

}
