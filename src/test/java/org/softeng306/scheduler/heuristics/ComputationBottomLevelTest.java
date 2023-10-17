package org.softeng306.scheduler.heuristics;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.Test;
import org.softeng306.exception.FileNotFoundException;
import org.softeng306.exception.InvalidInputException;
import org.softeng306.fileio.IOImp;
import org.softeng306.graph.Task;
import org.softeng306.utility.ScheduleIOUtility;

import static org.junit.Assert.*;

public class ComputationBottomLevelTest {

    @Test
    public void testComputeBottomLevel() {
        SimpleDirectedWeightedGraph<Task, DefaultWeightedEdge> graph =
                new SimpleDirectedWeightedGraph<Task, DefaultWeightedEdge>
                        (DefaultWeightedEdge.class);

        Task a = new Task("A", 2);
        Task b = new Task("B", 3);
        Task c = new Task("C", 4);
        Task d = new Task("D", 2);

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);
        graph.addVertex(d);

        DefaultWeightedEdge edge1 = graph.addEdge(a, b);
        graph.setEdgeWeight(edge1, 1);
        DefaultWeightedEdge edge2 = graph.addEdge(a, c);
        graph.setEdgeWeight(edge2, 2);
        DefaultWeightedEdge edge3 = graph.addEdge(b, d);
        graph.setEdgeWeight(edge3, 2);
        DefaultWeightedEdge edge4 = graph.addEdge(c, d);
        graph.setEdgeWeight(edge4, 1);

        a.setSuccessors(Graphs.successorListOf(graph, a));
        b.setSuccessors(Graphs.successorListOf(graph, b));
        c.setSuccessors(Graphs.successorListOf(graph, c));
        d.setSuccessors(Graphs.successorListOf(graph, d));

        // should be 8 because maximum bottom level should from A -> C -> D
        assertEquals(8, ComputationBottomLevel.computeMaxBottomLevel(a));
    }
    
    @Test
    public void testComputeBottomLevel2() throws InvalidInputException, FileNotFoundException, java.io.FileNotFoundException {
        String[] file1 = {"src/main/resources/Nodes_11_OutTree.dot", "2"};
        IOImp ioImp = new IOImp(file1);
        Graph<Task, DefaultWeightedEdge> graph = ScheduleIOUtility.importTaskGraph(ioImp.getInputFilePath());
        assertNotNull(graph);

        for (Task task : graph.vertexSet()) {
            if (task.getName().equals("0")) {
                assertEquals(220, ComputationBottomLevel.computeMaxBottomLevel(task));
            }
        }

    }


}