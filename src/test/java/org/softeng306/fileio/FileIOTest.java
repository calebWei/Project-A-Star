package org.softeng306.fileio;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;
import org.softeng306.exception.InvalidInputException;
import org.softeng306.graph.Node;
import org.softeng306.utility.ScheduleIOUtility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Set;

public class FileIOTest {

    @Test
    public void testE1File() throws InvalidInputException, FileNotFoundException {
        //File arrays for input file locations
        String[] file1 = new String[]{"src/main/resources/e1.dot", "2", "-o", "me1", "-p", "4"};
        IOImp ioImp= new IOImp(file1);
        // test the input
        assertEquals(2,ioImp.getNumberOfProcessor());
        assertEquals("me1.dot",ioImp.getOutputFileName());
        assertEquals(4,ioImp.getNumberOfCores());

        Graph<Node, DefaultWeightedEdge> graph = ScheduleIOUtility.importTaskGraph(ioImp.getInputFilePath());
        assertNotNull(graph);

        Set<Node> nodeSet = graph.vertexSet();
        Set<DefaultWeightedEdge> edgeSet = graph.edgeSet();

        // CHeck if all the nodes and edges were added to the Graph
        assertEquals(4, nodeSet.size());
        assertEquals(4, edgeSet.size());

        // Check weight of Node a
        Node nodeA = getNodeByName(nodeSet, "a");
        assertNotNull(nodeA);
        assertEquals(2, nodeA.getWeight());

        // Check weight of Node c
        Node nodeC = getNodeByName(nodeSet, "c");
        assertNotNull(nodeC);
        assertEquals(3, nodeC.getWeight());

        // Check weight of edge a -> c
        DefaultWeightedEdge edgeFromAToC = Objects.requireNonNull(graph.getEdge(nodeA, nodeC));
        assertEquals(2.0, graph.getEdgeWeight(edgeFromAToC), 0.001);
    }

    /**
     * This is a helper method to get our custom Node object from the JGraphT node set from a name.
     * @param nodeSet set of nodes
     * @param nodeName name of node to find
     * @return node object
     */
    private Node getNodeByName(Set<Node> nodeSet, String nodeName) {
        for (Node node : nodeSet) {
            if (node.getName().equals(nodeName)) {
                return node;
            }
        }
        return null;
    }




}
