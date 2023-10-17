package org.softeng306.visualization;

import javafx.beans.property.SimpleStringProperty;

import java.util.List;

/**
 * The ScheduleRow class is the rows in scheduling table
 */
public class ScheduleRow {
    List<String> taskNames;
    SimpleStringProperty startTime = new SimpleStringProperty();

    public ScheduleRow(List<String> taskNames, int startTime){
        this.taskNames = taskNames;
        this.startTime.set(Integer.toString(startTime));
    }

    public String getTaskName(int proc){
        return taskNames.get(proc);
    }

    public SimpleStringProperty startTimeProperty(){
        return startTime;
    }

}
