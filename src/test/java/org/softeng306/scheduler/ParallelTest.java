package org.softeng306.scheduler;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.softeng306.exception.InvalidInputException;
import org.softeng306.fileio.IOImp;
import org.softeng306.graph.Task;
import org.softeng306.utility.ScheduleIOUtility;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
@RunWith(Parameterized.class)
public class ParallelTest {
    private IOImp ioImp;
    private Graph<Task, DefaultWeightedEdge> graph;
    private ParallelScheduler parallelScheduler;

    private final String[] file;
    private final int expectedMakeSpan;

    public ParallelTest(String[] file, int expectedMakeSpan) {
        this.file = file;
        this.expectedMakeSpan = expectedMakeSpan;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {new String[]{"src/main/resources/e1.dot", "2", "-p", "4"}, 8},
                {new String[]{"src/main/resources/Nodes_7_OutTree.dot", "2", "-p", "4"}, 28},
                {new String[]{"src/main/resources/Nodes_7_OutTree.dot", "4", "-p", "4"}, 22},
                {new String[]{"src/main/resources/Nodes_8_Random.dot", "2", "-p", "4"}, 581},
                {new String[]{"src/main/resources/Nodes_8_Random.dot", "4", "-p", "4"}, 581},
                {new String[]{"src/main/resources/Nodes_9_SeriesParallel.dot", "2", "-p", "4"}, 55},
                {new String[]{"src/main/resources/Nodes_9_SeriesParallel.dot", "4", "-p", "4"}, 55},
                {new String[]{"src/main/resources/Nodes_10_Random.dot", "2", "-p", "4"}, 50},
                {new String[]{"src/main/resources/Nodes_10_Random.dot", "4", "-p", "4"}, 50},
                {new String[]{"src/main/resources/Nodes_11_OutTree.dot", "2", "-p", "4"}, 350},
                {new String[]{"src/main/resources/Nodes_11_OutTree.dot", "4", "-p", "4"}, 227},
        });
    }

    @Before
    public void setup() throws InvalidInputException, FileNotFoundException {
        parallelScheduler = new ParallelScheduler();
        ioImp = new IOImp(file);
        graph = ScheduleIOUtility.importTaskGraph(ioImp.getInputFilePath());
        assertNotNull(graph);
    }

    @Test
    public void testParallelScheduler() throws InvalidInputException, FileNotFoundException {
        ISchedule schedule = parallelScheduler.search(graph, ioImp.getNumberOfProcessor(), ioImp.getNumberOfCores());
        assertEquals(expectedMakeSpan, schedule.getMakeSpan());
    }
}