package org.softeng306.scheduler;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.softeng306.graph.Task;

import java.util.Comparator;

/**
 * This class contains the logic for sorting the Fixed Task Ordering list.
 * Second part, Condition 2 (Task sorting).
 */
public class FTOTaskComparator implements Comparator<Task> {
    private final Graph<Task, DefaultWeightedEdge> inputGraph;
    public FTOTaskComparator(Graph<Task, DefaultWeightedEdge> inputGraph) {
        this.inputGraph = inputGraph;
    }
    @Override
    public int compare(Task task1, Task task2) {
        //calculate data ready time for both tasks
        Task parentTask1 = (!task1.getPrerequisites().isEmpty()) ? task1.getPrerequisites().get(0) : null;
        Task parentTask2 = (!task2.getPrerequisites().isEmpty()) ? task2.getPrerequisites().get(0) : null;

        //data ready time of task ni of free(s) is the finish time of the parent np plus the weight of the edge epi.
        //without a parent, this time is set to zero.
        int dataReadyTime1 = (parentTask1 != null) ? (int) (parentTask1.getFinishTime() +
                inputGraph.getEdgeWeight(inputGraph.getEdge(parentTask1, task1))) : 0;
        int dataReadyTime2 = (parentTask2 != null) ? (int) (parentTask2.getFinishTime() +
                inputGraph.getEdgeWeight(inputGraph.getEdge(parentTask2, task2))) : 0;


        //compare based on data ready time (non-decreasing order)
        int dataReadyTimeComparison = Integer.compare(dataReadyTime1, dataReadyTime2);


        if (dataReadyTimeComparison != 0) {
            return dataReadyTimeComparison;
        } else {
            //break ties by sorting according to non-increasing out-edge costs
            //c(eic). If there is no out-edge, set cost to zero.
            Task childTask1 = (!task1.getSuccessors().isEmpty()) ? task1.getSuccessors().get(0) : null;
            Task childTask2 = (!task2.getSuccessors().isEmpty()) ? task2.getSuccessors().get(0) : null;

            int outEdgeCost1 = (childTask1 != null) ? (int) (inputGraph.getEdgeWeight(inputGraph.getEdge(task1, childTask1))) : 0;
            int outEdgeCost2 = (childTask2 != null) ? (int) (inputGraph.getEdgeWeight(inputGraph.getEdge(task1, childTask2))) : 0;

            //compare based on out-edge cost (edge of nf to its child) (non-increasing order)
            return Integer.compare(outEdgeCost2, outEdgeCost1);
        }
    }
}
