package org.softeng306.utility;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.softeng306.graph.Task;
import org.softeng306.scheduler.ISchedule;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides utility methods for input & output of the algorithm
 */
public class ScheduleIOUtility {
    static List<String>edges = new ArrayList<>();
    static int totalWeight =0;
    //   Regex to match the node (Task) and edge definitions in the .dot file.
    private static final Pattern nodePattern = Pattern.compile("\\s*([a-zA-Z0-9]+)\\s*\\[Weight=(\\d+)];");
    private static final Pattern edgePattern = Pattern.compile("\\s*([a-zA-Z0-9]+)\\s*(?:−|-|->)\\s*([a-zA-Z0-9]+)\\s*\\[Weight=(\\d+)];");


    /**
     * Private constructor to prevent instantiation
     */
    private ScheduleIOUtility() {}

    /**
     * This method parses an input graph in dot format and returns a Graph object.
     *
     * @param inputFilePath input file path
     * @return A weighted graph object representing the input dot graph
     */
    public static Graph importTaskGraph(String inputFilePath) throws FileNotFoundException {
        SimpleDirectedWeightedGraph<Task, DefaultWeightedEdge> graph =
                new SimpleDirectedWeightedGraph<>
                        (DefaultWeightedEdge.class);
        Map<String, Task> nameToTask = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Ignore empty lines
                if (line.isEmpty()) {
                    continue;
                }

                // May want to handle the Graph name if needed...

                // Try to match both node (Task) and edge patterns
                Matcher edgeMatcher = edgePattern.matcher(line);
                Matcher nodeMatcher = nodePattern.matcher(line);

                // Check if the input matches the node pattern e.g. "a [Weight=2];"
                if (nodeMatcher.matches()) {
                    String taskName = nodeMatcher.group(1);
                    int weight = Integer.parseInt(nodeMatcher.group(2));
                    totalWeight+=weight;

                    Task task = new Task(taskName, weight);

                    graph.addVertex(task);

                    // Store the Task in the map so that we can reference the same Task object later for when we add edges.
                    nameToTask.put(taskName, task);
                }
                // Check if the input matches the edge pattern e.g. "a -> b [Weight=2];"
                else if (edgeMatcher.matches()) {
                    String source = edgeMatcher.group(1);
                    String destination = edgeMatcher.group(2);
                    int weight = Integer.parseInt(edgeMatcher.group(3));
                    edges.add(line);
                    // Create a weight edge in the graph between those task (they should be already placed within the
                    // graph) from graph.addVertex()
                    Task sourceTask = nameToTask.get(source);
                    Task destinationTask = nameToTask.get(destination);
                    DefaultWeightedEdge edge = graph.addEdge(sourceTask, destinationTask);
                    graph.setEdgeWeight(edge, weight);
                }
            }

            // Set pre-requisites for each Task
            for (Task task : graph.vertexSet()) {
                task.setPrerequisites(Graphs.predecessorListOf(graph, task));
                task.setSuccessors(Graphs.successorListOf(graph, task));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return graph;
    }

    /**
     * Take a schedule and file path, convert the schedule into an output dot file.
     *
     * @param schedule the complete schedule that has an optimal path
     * @param filePath could get from the io function  String getOutputFilePath();
     */
    public static void exportSchedule(ISchedule schedule, String filePath) {
        // Save output as a dot format file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Extract the name of the output file without the extension
            String outputGraphName = new File(filePath).getName().split("\\.")[0];

            writer.write("digraph \"" + outputGraphName + "\" {\n");
            for(String edge : edges){
                writer.write("\t"+edge+"\t"+"\n");
            }
            // Write tasks
            for (Task task : schedule.getScheduledTasks()) {
                writer.write(String.format("\t%s\t [Weight=%d,Start=%d,Processor=%d];%n",
                        task.getName(), task.getWeight(), task.getStartTime(), task.getProcessorNumber() + 1));
            }

            writer.write("}\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getTotalWeight() {
        return totalWeight;
    }
}
