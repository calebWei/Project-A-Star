package org.softeng306.visualization;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.softeng306.graph.Task;
import org.softeng306.scheduler.ISchedule;

public class GraphTransform {
        private SingleGraph graphStream;
        private ISchedule currentSchedule;
        private final Graph<Task, DefaultWeightedEdge> graph;

        public GraphTransform(ISchedule currentSchedule, Graph<Task, DefaultWeightedEdge> graph) {
                this.currentSchedule = currentSchedule;
                this.graph = graph;
                createGraphStream();
        }

        public SingleGraph getGraphStream() {
                return graphStream;
        }

        /**
         * the function is change the current shcedule to the graph stream
         */
        private void createGraphStream() {
                //reset the graph to the graph stream graph
                //change the node to the graphStream node
                graphStream = new SingleGraph("graph");

                if (currentSchedule != null) {
                        for (Task scheduledInputTask : currentSchedule.getScheduledTasks()) {
                                if (graphStream.getNode(scheduledInputTask.getName()) == null) {
                                        Node nodeGraphStream = graphStream.addNode(scheduledInputTask.getName());
                                        nodeGraphStream.setAttribute("weight", scheduledInputTask.getWeight());
                                        nodeGraphStream.setAttribute("processor", scheduledInputTask.getProcessorNumber());
                                        nodeGraphStream.setAttribute("startTime", scheduledInputTask.getStartTime());
                                }
                        }

                        // change the edge to the graphStream edge
                        int edgeID = 0;
                        for (DefaultWeightedEdge edge : graph.edgeSet()) {
                                edgeID++;
                                Task sourceVertex = graph.getEdgeSource(edge);
                                Task targetVertex = graph.getEdgeTarget(edge);
                                /////
                                Node sourceNodeStream = graphStream.getNode(sourceVertex.getName());
                                Node targetNodeStream = graphStream.getNode(targetVertex.getName());
                                if (sourceNodeStream != null && targetNodeStream != null) {
                                        Edge edgeStream = graphStream.addEdge(String.valueOf(edgeID), sourceNodeStream, targetNodeStream, true);
                                        edgeStream.setAttribute("weight", graph.getEdgeWeight(edge));
                                }
                        }
                }

        }

        /**
         * the function is used when to change the graph to show
         *
         * @param scheduleInput schedule to show
         */
        public void updateGraphStream(ISchedule scheduleInput) {
                currentSchedule = scheduleInput;
                createGraphStream();
        }
}
