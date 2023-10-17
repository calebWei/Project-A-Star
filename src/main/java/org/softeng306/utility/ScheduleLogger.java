package org.softeng306.utility;

import org.softeng306.graph.Task;
import org.softeng306.scheduler.ISchedule;

import java.util.*;

public class ScheduleLogger {

    public static void printSchedule(ISchedule schedule){

        Set<Task> tasks = schedule.getScheduledTasks();
        int procs = schedule.getNumProcessors();
        List<SortedMap<Integer, Task>> procScheduleList = new ArrayList<>();

        StringBuilder borderBuiler = new StringBuilder();
        for(int i = 0; i < procs + 1; i++){
            borderBuiler.append("-------");
        }
        borderBuiler.append("-");
        System.out.println(borderBuiler);

        StringBuilder titleBuilder = new StringBuilder();
        titleBuilder.append(String.format("| %-5s", "Time"));
        for(int i = 0; i < procs; i++){
            titleBuilder.append(String.format("| P%-4s", i + 1));
        }
        titleBuilder.append("|");
        System.out.println(titleBuilder);
        System.out.println(borderBuiler);

        for(int i = 0; i < procs; i++){
            SortedMap<Integer, Task> procTasks = new TreeMap<>();
            for(Task t : tasks){
                if(t.getProcessorNumber() == i){
                    for(int j = t.getStartTime(); j < t.getFinishTime(); j++){
                        procTasks.put(j, t);
                    }
                }
            }
            procScheduleList.add(procTasks);
        }

        for(int t = 0; t <= schedule.getMakeSpan(); t++){
            StringBuilder lineBuilder = new StringBuilder();
            lineBuilder.append(String.format("| %-5s", t));
            for(int p = 0; p < procs; p++){
                if(procScheduleList.get(p).containsKey(t)){
                    Task task = procScheduleList.get(p).get(t);
                    lineBuilder.append(String.format("| %-5s", task.getName()));
                } else {
                    lineBuilder.append(String.format("| %-5s", "IDLE"));
                }
            }
            lineBuilder.append("|");
            System.out.println(lineBuilder);
        }
        System.out.println(borderBuiler);
    }
}