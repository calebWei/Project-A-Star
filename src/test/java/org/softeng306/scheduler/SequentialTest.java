package org.softeng306.scheduler;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.manipulation.Orderable;
import org.junit.runners.MethodSorters;
import org.softeng306.exception.InvalidInputException;
import org.softeng306.fileio.IOImp;
import org.softeng306.graph.Task;
import org.softeng306.utility.ScheduleIOUtility;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
public class SequentialTest {


    @Test
    public void test1_e1Pro2() throws InvalidInputException, FileNotFoundException {
        String[] file1 = {"src/main/resources/e1.dot", "2"};
        IOImp ioImp = new IOImp(file1);
        Graph<Task, DefaultWeightedEdge> graph1 = ScheduleIOUtility.importTaskGraph(ioImp.getInputFilePath());
        assertNotNull(graph1);

        int numProcessors = ioImp.getNumberOfProcessor();
        int numCores = ioImp.getNumberOfCores();
        SequentialScheduler sequentialScheduler = new SequentialScheduler();
        ISchedule schedule = sequentialScheduler.search(graph1, numProcessors, numCores);
        int best = sequentialScheduler.getOrderedClosedSet().get(sequentialScheduler.getOrderedClosedSet().size()-1).getSchedule().getMakeSpan();

        assertEquals(8, best);
    }

    @Test
    public void test2_Node7Pro2() throws InvalidInputException, FileNotFoundException {
        String[] file1 = {"src/main/resources/Nodes_7_OutTree.dot", "2"};
        IOImp ioImp = new IOImp(file1);
        Graph<Task, DefaultWeightedEdge> graph = ScheduleIOUtility.importTaskGraph(ioImp.getInputFilePath());
        assertNotNull(graph);

        int numProcessors = ioImp.getNumberOfProcessor();
        int numCores = ioImp.getNumberOfCores();
        SequentialScheduler sequentialScheduler = new SequentialScheduler();
        ISchedule schedule = sequentialScheduler.search(graph, numProcessors, numCores);
        int best = schedule.getMakeSpan();

        assertEquals(28, best);
    }

    @Test
    public void test3_Node8Pro2() throws InvalidInputException, FileNotFoundException {
        String[] file1 = {"src/main/resources/Nodes_8_Random.dot", "2"};
        IOImp ioImp = new IOImp(file1);
        Graph<Task, DefaultWeightedEdge> graph = ScheduleIOUtility.importTaskGraph(ioImp.getInputFilePath());
        assertNotNull(graph);

        int numProcessors = ioImp.getNumberOfProcessor();
        int numCores = ioImp.getNumberOfCores();
        SequentialScheduler sequentialScheduler = new SequentialScheduler();
        ISchedule schedule = sequentialScheduler.search(graph, numProcessors, numCores);
        int best = schedule.getMakeSpan();

        assertEquals(581, best);
    }
    @Test
    public void test4_Node9Pro2() throws InvalidInputException, FileNotFoundException {
        String[] file1 = {"src/main/resources/Nodes_9_SeriesParallel.dot", "2"};
        IOImp ioImp = new IOImp(file1);
        Graph<Task, DefaultWeightedEdge> graph = ScheduleIOUtility.importTaskGraph(ioImp.getInputFilePath());
        assertNotNull(graph);

        int numProcessors = ioImp.getNumberOfProcessor();
        int numCores = ioImp.getNumberOfCores();
        SequentialScheduler sequentialScheduler = new SequentialScheduler();
        ISchedule schedule = sequentialScheduler.search(graph, numProcessors, numCores);
        int best = schedule.getMakeSpan();

        assertEquals(55, best);
    }



    @Test
    public void test6_Node8Pro4() throws InvalidInputException, FileNotFoundException {
        String[] file1 = {"src/main/resources/Nodes_8_Random.dot", "4"};
        IOImp ioImp = new IOImp(file1);
        Graph<Task, DefaultWeightedEdge> graph = ScheduleIOUtility.importTaskGraph(ioImp.getInputFilePath());
        assertNotNull(graph);

        int numProcessors = ioImp.getNumberOfProcessor();
        int numCores = ioImp.getNumberOfCores();
        SequentialScheduler sequentialScheduler = new SequentialScheduler();
        ISchedule schedule = sequentialScheduler.search(graph, numProcessors, numCores);
        int best = schedule.getMakeSpan();

        assertEquals(581, best);
    }
    @Test
    public void test7_Node9Pro4() throws InvalidInputException, FileNotFoundException {
        String[] file1 = {"src/main/resources/Nodes_9_SeriesParallel.dot", "4"};
        IOImp ioImp = new IOImp(file1);
        Graph<Task, DefaultWeightedEdge> graph = ScheduleIOUtility.importTaskGraph(ioImp.getInputFilePath());
        assertNotNull(graph);

        int numProcessors = ioImp.getNumberOfProcessor();
        int numCores = ioImp.getNumberOfCores();
        SequentialScheduler sequentialScheduler = new SequentialScheduler();
        ISchedule schedule = sequentialScheduler.search(graph, numProcessors, numCores);
        int best = schedule.getMakeSpan();

        assertEquals(55, best);
    }



}
