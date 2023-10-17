package org.softeng306.scheduler.heuristics;

import org.softeng306.graph.Task;
import org.softeng306.scheduler.ScheduleTreeNode;
import org.softeng306.utility.ScheduleIOUtility;

public class IdleTime {

    /**
     * Private constructor to prevent instantiation
     */
    private IdleTime() {}

    /**
     * Set the idle time to a schedule
     *
     * @param numProcessors number of processors
     * @param currentNode the current partial schedule we are at from the decision tree
     * @return idle time
     */
    public static double getIdleTime(int numProcessors, ScheduleTreeNode currentNode){
        int totalWeight = ScheduleIOUtility.getTotalWeight();
        //All processors idle time
        int idles =0;
        for (int procID = 0; procID < numProcessors; procID++) {
            int processorTaskTime =0;

            for(Task task:currentNode.getSchedule().getScheduledTasks()){
                if(task.getProcessorNumber()==procID){
                    processorTaskTime += task.getWeight();
                }
            }

            int lastFinishTime = currentNode.getSchedule().getProcFreeTime(procID);
            //individual processor idle time
            int idle = lastFinishTime-processorTaskTime;
            idles += idle;
        }

        return (double) (idles+totalWeight)/numProcessors;
    }

}
