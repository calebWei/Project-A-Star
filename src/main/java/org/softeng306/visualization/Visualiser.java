package org.softeng306.visualization;

import atlantafx.base.theme.Dracula;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.softeng306.fileio.IOImp;
import org.softeng306.graph.Task;
import org.softeng306.scheduler.thread.SchedulerThread;

import java.io.IOException;
import java.util.Objects;

public class Visualiser extends Application {
    public static void main(String[] args) {
        launch();
    }

    private VisualisationController controller;

    @Override
    public void start (Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/visualization.fxml"));
        Parent root = loader.load();
        this.controller = loader.getController();
        Scene scene = new Scene(root, 1190, 765);
        stage.setScene(scene);
        stage.setOnCloseRequest(e ->{
            Platform.exit();
            System.exit(0);
        });
        stage.getIcons().add(new Image(Objects.requireNonNull(Visualiser.class.getResourceAsStream("/icon.png"))));
        stage.setTitle("Task scheduling");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }
    public void initialSetup(SchedulerThread schedulerThread, IOImp io, Graph<Task, DefaultWeightedEdge> graph) {
        this.controller.initialSetup(schedulerThread, io, graph);
    }
}
