package org.softeng306;
import com.sun.javafx.application.PlatformImpl;
import javafx.stage.Stage;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.softeng306.exception.InvalidInputException;
import org.softeng306.fileio.DisplayMode;
import org.softeng306.fileio.IOImp;
import org.softeng306.graph.Task;
import org.softeng306.scheduler.*;
import org.softeng306.scheduler.thread.SchedulerThread;
import org.softeng306.utility.ScheduleIOUtility;
import org.softeng306.visualization.Visualiser;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

public class App
{
    private static final Logger logger = Logger.getLogger(App.class.getName());
    private static IOImp io;
    private static Graph<Task, DefaultWeightedEdge> graph;
    private static Scheduler scheduler;

    /**
     * Entry point of the Scheduler
     * @param args commandline arguments
     */
    public static void main(String[] args)
    {
        try {
            // Get graph from file
            io= new IOImp(args);
            graph = ScheduleIOUtility.importTaskGraph(io.getInputFilePath());

            // Generate Schedule
            if (io.getNumberOfCores() == 1) {
                scheduler = new SequentialScheduler();
            } else {
                scheduler = new ParallelScheduler();
            }

            // run the algorithm
            if (io.getVisualisationState() == DisplayMode.VISUALISE) {
                runVisualSearch();
            }
            else {
                runCommandLineSearch();
            }
        } catch (InvalidInputException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void runCommandLineSearch() {
        long startTime = System.currentTimeMillis();
        ISchedule schedule = scheduler.search(graph, io.getNumberOfProcessor(), io.getNumberOfCores());
        logger.info("Task scheduling took: " + (System.currentTimeMillis() - startTime) + "ms");
        logger.info("The best schedule has a finishing time of " + schedule.getMakeSpan());
        ScheduleIOUtility.exportSchedule(schedule, io.getOutputFilePath());
    }

    private static void runVisualSearch() {
        PlatformImpl.startup(() -> {
            SchedulerThread schedulerThread = new SchedulerThread(scheduler, io, graph);
            Visualiser visualiser = new Visualiser();
            try {
                visualiser.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
            visualiser.initialSetup(schedulerThread, io, graph);
        });
    }

}