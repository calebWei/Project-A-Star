package org.softeng306.visualization;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.softeng306.fileio.IO;
import org.softeng306.fileio.IOImp;
import org.softeng306.graph.Task;
import org.softeng306.scheduler.ISchedule;
import org.softeng306.scheduler.ScheduleTreeNode;
import org.softeng306.scheduler.thread.SchedulerThread;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class VisualisationController {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private Graph<Task, DefaultWeightedEdge> graph;
    private SchedulerThread schedulerThread;
    private IO io;
    private List<ScheduleTreeNode> orderedPartialSchedules = new ArrayList<>();
    private String executionTime;
    private final List<String> containerColourList = new ArrayList<>();
    private GraphTransform graphTransform;
    private SingleGraph graphStream;
    private GraphUpdate graphUpdate;
    private GanttChart<Number, String> ganttChart;
    private NumberAxis xAxis;
    private ViewPanel viewPanel;
    private int maxIteration = 0;
    private int currentIteration = 0;
    private int numCopy = 0;
    private double initialX;
    private double initialY;
    private boolean hasUsedIteration = false;
    @FXML private Text fileNameText;
    @FXML private Text startText;
    @FXML private TextField textFieldNum;
    @FXML private TableView<TaskModel> taskTableView;
    @FXML private TableView<ScheduleRow> scheduleTableView;
    @FXML private TableColumn<TaskModel, String>nodeColumn;
    @FXML private TableColumn<TaskModel, Integer>taskStartTimeColumn;
    @FXML private TableColumn<TaskModel, Integer>procColumn;
    @FXML private SwingNode swingNode;
    @FXML private Pane ganttChartPane;
    @FXML private Pane elevationPane;
    @FXML private ScrollPane legendPane;
    @FXML private StackPane notificationContainer;
    @FXML private AnchorPane topBar;
    @FXML private AnchorPane memoryPane;
    @FXML private HBox legendHBox;
    @FXML private HBox hBox;
    @FXML private Button lastIteration;
    @FXML private Button nextIteration;
    @FXML private Button finishIteration;
    @FXML private Button startBtn;
    @FXML private ToggleButton infoButton;
    @FXML private ToggleButton moveButton;
    @FXML private Label invalid;
    @FXML private Tab resultTab;
    @FXML private Tab graphTab;
    @FXML private Tab ganttTab;
    @FXML private ToolBar sideToolBar;
    @FXML private Label statusLabel;
    @FXML private Label fileNameLabel;
    @FXML private Label numOfTasksLabel;
    @FXML private Label numOfProcessorsLabel;
    @FXML private Label executionTimeLabel;
    @FXML private Label numOfCoresLabel;
    @FXML private Label numOfStatesExaminedLabel;
    @FXML private Label outputFileNameLabel;
    @FXML private Label currentMakeSpanLabel;
    @FXML private DoughnutChart memoryChart;

    /**
     * Initializes the main components and user interface elements of the application.
     *
     * @param schedulerThread The scheduler thread responsible for task scheduling.
     * @param io The I/O implementation for handling input and output.
     * @param graph The graph representing the tasks and their dependencies.
     */
    public void initialSetup(SchedulerThread schedulerThread, IOImp io, Graph<Task, DefaultWeightedEdge> graph){
        this.schedulerThread = schedulerThread;
        this.io = io;
        this.graph = graph;

        // set up the constant labels
        numOfTasksLabel.setText(String.valueOf(graph.vertexSet().size()));
        fileNameLabel.setText(io.getInputFileName());
        outputFileNameLabel.setText(io.getOutputFileName());
        numOfProcessorsLabel.setText(String.valueOf(io.getNumberOfProcessor()));
        numOfCoresLabel.setText(String.valueOf(io.getNumberOfCores()));

        // set up tab icons
        resultTab.setGraphic(new FontIcon(MaterialDesign.MDI_TABLE));
        graphTab.setGraphic(new FontIcon(MaterialDesign.MDI_CHART_BUBBLE));
        ganttTab.setGraphic(new FontIcon(MaterialDesign.MDI_CHART_GANTT));

        // set up the visuals for the side control bar, custom window border, and memory chart
        initialiseSideToolBar();
        initialiseTopBar();
        initialiseMemoryDoughnutChart();

        // Set initial state...what even is this
        invalid.setVisible(false);

        lastIteration.setDisable(true);
        nextIteration.setDisable(true);
        finishIteration.setDisable(true);

        // ensures only numbers can be typed into the goto field
        textFieldNum.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!isNumeric(event.getCharacter())) {
                event.consume();
            }
        });

        // set to 0 first...
        textFieldNum.setText("0 ~ "+ 0);


        legendPane.setOnScroll(event -> {
            if (event.getDeltaX() == 0 && event.getDeltaY() != 0) {
                legendPane.setHvalue(legendPane.getHvalue() - event.getDeltaY() / legendPane.getWidth());
                event.consume();
            }
        });

        // create initial (empty) gantt chart
        createGanttChart();

    }

    /**
     * Initiates the execution of the task scheduling process and updates the user interface during the process.
     * The method is called when the "Start" button is pressed.
     */
    @FXML
    private void onStart() {
        startBtn.setDisable(true);
        startText.setVisible(false);
        statusLabel.setText("RUNNING");
        statusLabel.setTextFill(Color.ORANGE);
        schedulerThread.start();

        graphTransform = new GraphTransform(null, graph);
        // polling and updating relevant displayed info
        new AnimationTimer() {
            final long startTime = System.currentTimeMillis();
            private long lastUpdate = 0;
            @Override
            public void handle(long now) {
                long elapsedMillis = System.currentTimeMillis() - startTime;
                int milliseconds = (int) ( elapsedMillis % 1000);
                int seconds = (int) ((elapsedMillis / 1000) % 60);
                int minutes = (int) ((elapsedMillis / (1000 * 60)) % 60);
                int hours = (int) (elapsedMillis / (1000 * 60 * 60));
                executionTime = String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, milliseconds / 10);
                executionTimeLabel.setText(executionTime);
                //keep checking if search is done
                if (schedulerThread.isDone()) {
                    this.stop();
                    statusLabel.setText("COMPLETE");
                    statusLabel.setTextFill(Color.LIGHTGREEN);
                    showNotification();
                    logger.info("Task scheduling took: " + (elapsedMillis) + "ms");
                    logger.info("The best schedule has a finishing time of " + schedulerThread.getCurrentSchedule().getSchedule().getMakeSpan());
                }
                // otherwise only update every 1 second
                else if (now - lastUpdate < 1_000_000_000) {
                    return;
                } else {
                    lastUpdate = now;
                }

                // update total states examined and memory chart
                numOfStatesExaminedLabel.setText(String.valueOf(schedulerThread.getNumberOfStatesExamined() - 1));

                // update our list of all partial schedules (and potentially complete schedule at end)
                orderedPartialSchedules = schedulerThread.getOrderedClosedSet();
                int oldMaxIteration = maxIteration;
                maxIteration = orderedPartialSchedules.size() - 1;

                //show the latest schedule in the search only if the user was on the latest before
                if (currentIteration == oldMaxIteration) {
                    //update the currentIteration to the new max value for next poll
                    currentIteration = maxIteration;
                    ISchedule currentSchedule = schedulerThread.getCurrentSchedule().getSchedule();
                    updateSchedule(currentSchedule);
                    lastIteration.setDisable(false);
                }
                else if (maxIteration > currentIteration) {
                    nextIteration.setDisable(false);
                    finishIteration.setDisable(false);
                }
                if (!hasUsedIteration) {
                    textFieldNum.setText("0 ~ "+ maxIteration);
                }
            }
        }.start();
    }

    /**
     * Updates the displayed schedule with the given current schedule, and refreshes various visuals in the user interface.
     *
     * @param currentSchedule The current schedule to be displayed and updated.
     */
    private void updateSchedule(ISchedule currentSchedule) {
        currentMakeSpanLabel.setText(String.valueOf(currentSchedule.getMakeSpan()));
        // update the displayed visuals like gantt chart, graph, and tables
        updateGanttChartContent(currentSchedule);
        updateGraph(currentSchedule);
        updateTaskTable(currentSchedule);
        updateDynamicSchedulingTable(io.getNumberOfProcessor(), currentSchedule);
    }

    /**
     * Initializes the top bar of the application's user interface. This includes setting up drag listeners,
     * displaying the input file name, and creating close and minimize buttons for the window.
     */
    private void initialiseTopBar() {
        addDragListeners(topBar);
        addDragListeners(elevationPane);
        fileNameText.setText(io.getInputFileName());
        fileNameText.getStyleClass().addAll(Styles.TEXT, Styles.TEXT_SUBTLE, Styles.TEXT_ITALIC);
        Button closeButton = new Button(null, new FontIcon(MaterialDesign.MDI_WINDOW_CLOSE));
        closeButton.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT, Styles.DANGER);
        closeButton.setPrefHeight(44);
        closeButton.setPrefWidth(88);
        closeButton.setOnAction(e -> {
            Window window = closeButton.getScene().getWindow();
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        Button minimiseButton = new Button(null, new FontIcon(MaterialDesign.MDI_WINDOW_MINIMIZE));
        minimiseButton.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT, Styles.ACCENT);
        minimiseButton.setPrefHeight(44);
        minimiseButton.setPrefWidth(88);
        minimiseButton.setOnAction(e -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.setIconified(true);
        });
        hBox.getChildren().add(minimiseButton);
        hBox.getChildren().add(closeButton);
        elevationPane.getStyleClass().add(Styles.ELEVATED_1);
    }

    /**
     * Initializes the side toolbar of the application's user interface. This includes creating and configuring toggle buttons
     * for information display, graph movement, and navigation between explored states, as well as styling the toolbar.
     */
    private void initialiseSideToolBar() {
        infoButton = new ToggleButton(null, new FontIcon(MaterialDesign.MDI_INFORMATION_OUTLINE));
        infoButton.getStyleClass().addAll(
                Styles.BUTTON_ICON, Styles.FLAT, Styles.ACCENT);
        infoButton.setOnAction(this::onClickInfoButton);
        infoButton.setPrefWidth(41);
        infoButton.setPrefHeight(41);
        infoButton.setTooltip(new Tooltip("View Task's assigned processor and duration."));

        moveButton = new ToggleButton(null, new FontIcon(MaterialDesign.MDI_CURSOR_MOVE));
        moveButton.getStyleClass().addAll(
                Styles.BUTTON_ICON, Styles.FLAT, Styles.ACCENT);
        moveButton.setOnAction(this::onClickMoveButton);
        moveButton.setPrefWidth(41);
        moveButton.setPrefHeight(41);
        moveButton.setTooltip(new Tooltip("Toggle graph movement."));

        nextIteration = new Button(null, new FontIcon(MaterialDesign.MDI_SUBDIRECTORY_ARROW_RIGHT));
        nextIteration.getStyleClass().addAll(
                Styles.BUTTON_ICON, Styles.FLAT, Styles.ACCENT);
        nextIteration.setOnAction(this::onClickNextIteration);
        nextIteration.setDisable(true);
        nextIteration.setPrefWidth(41);
        nextIteration.setPrefHeight(41);
        nextIteration.setTooltip(new Tooltip("View next state explored."));

        lastIteration = new Button(null, new FontIcon(MaterialDesign.MDI_SUBDIRECTORY_ARROW_LEFT));
        lastIteration.getStyleClass().addAll(
                Styles.BUTTON_ICON, Styles.FLAT, Styles.ACCENT);
        lastIteration.setOnAction(this::onClickLastIteration);
        lastIteration.setPrefWidth(41);
        lastIteration.setPrefHeight(41);
        lastIteration.setTooltip(new Tooltip("View previous state explored."));

        finishIteration = new Button(null, new FontIcon(MaterialDesign.MDI_SKIP_FORWARD));
        finishIteration.getStyleClass().addAll(
                Styles.BUTTON_ICON, Styles.FLAT, Styles.ACCENT);
        finishIteration.setOnAction(this::onClickFinishIteration);
        finishIteration.setPrefWidth(41);
        finishIteration.setPrefHeight(41);
        finishIteration.setTooltip(new Tooltip("View complete schedule."));

        sideToolBar.getItems().add(infoButton);
        sideToolBar.getItems().add(moveButton);
        sideToolBar.getItems().add(new Separator(Orientation.HORIZONTAL));
        sideToolBar.getItems().add(finishIteration);
        sideToolBar.getItems().add(nextIteration);
        sideToolBar.getItems().add(lastIteration);
        sideToolBar.getStyleClass().add(Styles.ELEVATED_1);
    }

    /**
     * Initializes the memory doughnut chart in the application's user interface. This chart displays memory usage information,
     * updates it periodically, and sets its visual properties such as title, size, and visibility of the legend and labels.
     */
    private void initialiseMemoryDoughnutChart() {
        memoryChart = new DoughnutChart();
        memoryChart.setTitle("Memory Usage");
        memoryChart.setPrefWidth(300);
        memoryChart.setPrefHeight(300);
        memoryChart.setLegendVisible(true);
        memoryChart.setLabelsVisible(false);
        memoryPane.getChildren().add(memoryChart);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> updateMemoryChart())
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * this method updates the memory doughnut chart
     */
    private void updateMemoryChart() {
        Runtime runtime = Runtime.getRuntime();
        // get the total memory available to the JVM in bytes
        long maxMemoryBytes = runtime.maxMemory();
        long totalMemoryBytes = runtime.totalMemory();
        long freeMemoryBytes = runtime.freeMemory();
        long usedMemoryBytes = totalMemoryBytes - freeMemoryBytes;

        double maxMemoryMB = maxMemoryBytes / (1024.0 * 1024);
        double usedMemoryMB = usedMemoryBytes / (1024.0 * 1024);

        double usedMemoryPercentage = ((double)usedMemoryBytes / maxMemoryBytes) * 100;
        double maxMemoryPercentage = (double) 100 - usedMemoryPercentage;

        String usedMemoryString = String.format("Used Memory, %.1fMB - %.2f%%%n", usedMemoryMB, usedMemoryPercentage);
        String maxMemoryString = String.format("Max Memory, %.1fMB - %.2f%%%n", maxMemoryMB, maxMemoryPercentage);

        if (!memoryChart.getData().isEmpty() && memoryChart.getData().size() == 2) {
            // update memory chart
            PieChart.Data usedMemorySlice = memoryChart.getData().get(0);
            usedMemorySlice.setName(usedMemoryString);
            usedMemorySlice.setPieValue(usedMemoryBytes);

            PieChart.Data maxMemorySlice = memoryChart.getData().get(1);
            maxMemorySlice.setName(maxMemoryString);
            maxMemorySlice.setPieValue(maxMemoryBytes);
        }
        else {
            PieChart.Data usedMemSlice = new PieChart.Data(usedMemoryString, usedMemoryBytes);
            PieChart.Data maxMemorySlice = new PieChart.Data(maxMemoryString, maxMemoryBytes);

            //clear display new data
            memoryChart.clearPieChartData();
            memoryChart.addPieChartData(usedMemSlice, maxMemorySlice);
        }
    }


    /**
     * Set and display the table containing all scheduled nodes with their corresponding information
     */
    private void updateTaskTable(ISchedule schedule){
        // Initialize column property
        nodeColumn.setCellValueFactory(new PropertyValueFactory<>("node"));
        taskStartTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        procColumn.setCellValueFactory(new PropertyValueFactory<>("procNumber"));

        // Initialize the list of table content
        ObservableList<TaskModel> tableContent = FXCollections.observableArrayList();

        // Sort all the scheduled tasks based on node name in ascending order
        List<Task> scheduledTasks = new ArrayList<>(schedule.getScheduledTasks());
        scheduledTasks.sort(Comparator.comparing(Task::getName));

        // Load all scheduled tasks to the content list
        for (Task task : scheduledTasks){
            TaskModel dataModel = new TaskModel(task.getName(), task.getStartTime(), task.getProcessorNumber()+1);
            tableContent.add(dataModel);
        }

        // Add table content
        taskTableView.setItems(tableContent);
    }

    /**
     * Initialize and/or update the scheduling table (task name on processor by time)
     */
    private void updateDynamicSchedulingTable(int numOfProc, ISchedule schedule){
        // == Generate Data for Table ==
        // Data can be regenerated for new partial schedule by calling initialize again
        ObservableList<ScheduleRow> scheduleData = FXCollections.observableArrayList();
        for(int i = 0; i < schedule.getMakeSpan(); i++){
            List<String> taskNames = new ArrayList<>();
            for(int j = 0; j < numOfProc; j++){
                taskNames.add(" ");
                for(Task task : schedule.getScheduledTasks()){
                    if(task.getProcessorNumber() == j && task.getPeriod().contains(i)){
                        taskNames.set(j, task.getName());
                    }
                }
            }
            ScheduleRow row = new ScheduleRow(taskNames, i);
            scheduleData.add(row);
        }
        scheduleTableView.setItems(scheduleData);

        // == Column Declarations ==
        // Only needed on first initialize
        if(scheduleTableView.getColumns().size() <= 1){
            // Time Column
            TableColumn<ScheduleRow, String> timeStepColumn = new TableColumn<>("Time");
            timeStepColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
            scheduleTableView.getColumns().add(timeStepColumn);
            timeStepColumn.setMinWidth(50);
            // Processor Columns
            for(int i = 0; i < numOfProc; i++){
                TableColumn<ScheduleRow, String> column = new TableColumn<>("P" + (i + 1));
                final int finalI = i;
                column.setCellValueFactory(scheduleRowStringCellDataFeatures -> new SimpleStringProperty(scheduleRowStringCellDataFeatures.getValue().getTaskName(finalI)));
                // Adds each column (in order of processor number)
                scheduleTableView.getColumns().add(column);
                column.setMinWidth(50);
            }
            scheduleTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }
    }

    /**
     * Initialized scheduling data
     */
    private void updateGraph(ISchedule schedule) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graphTransform.updateGraphStream(schedule);
        graphStream = graphTransform.getGraphStream();
        graphUpdate = new GraphUpdate(graphStream, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        graphUpdate.updateGraph(graphStream);
        graphUpdate.enableAutoLayout();
        infoButton.setSelected(false);
        moveButton.setSelected(false);

        // Create graphView panel
        viewPanel = graphUpdate.addDefaultView(false);
        viewPanel.setMinimumSize(new Dimension(851,685)); //Window size
        viewPanel.setOpaque(false);
        viewPanel.setBorder(BorderFactory.createLineBorder(java.awt.Color.BLACK));
        graphUpdate.setMouseManager(viewPanel);

        // Fade out effect for the SwingNode
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), swingNode);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> SwingUtilities.invokeLater(() -> {
            swingNode.setContent(viewPanel);

            // Fade in effect for the SwingNode
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), swingNode);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }));

        fadeOut.play();

        swingNode.setLayoutX(0);
        swingNode.setLayoutY(0);
    }


    /**
     * Create the gantt chart
     */
    private void createGanttChart(){
        initialiseContainerColour();
        // x-axis will change every update to match makespan
        xAxis = new NumberAxis();
        // y-axis will stay same
        CategoryAxis yAxis = new CategoryAxis();
        ganttChart = new GanttChart<>(xAxis, yAxis);

        // Retrieve all processor numbers
        List<String> procNumber = new ArrayList<>();
        for (int i = 1; i <= io.getNumberOfProcessor(); i++){
            procNumber.add(String.valueOf(i));
        }

        // Initialize x axis
        xAxis.setLabel("Time");
        xAxis.setLowerBound(0);

        // initial upper bound
        xAxis.setUpperBound(10);
        xAxis.setTickUnit(1);

        xAxis.setAutoRanging(false);
        xAxis.setMinorTickVisible(false);

        // Initialize y axis
        yAxis.setLabel("Processor");
        yAxis.setTickLabelGap(20);
        yAxis.setCategories(FXCollections.observableList(procNumber));

        // Initialize gantt chart
        ganttChart.setBlockHeight((double) 100 / io.getNumberOfProcessor());
        ganttChart.setPrefWidth(745);
        ganttChart.setPrefHeight(408);
        ganttChart.setLegendVisible(false);
        ganttChart.setHorizontalGridLinesVisible(false);
        ganttChart.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ganttChart.css")).toExternalForm());

        ganttChartPane.getChildren().add(ganttChart);
    }

    /**
     * Set Gantt chart content
     */
    private void updateGanttChartContent(ISchedule schedule){
        legendHBox.getChildren().clear();
        ganttChart.getData().clear();

        // Sort all the scheduled tasks based on node name in ascending order
        List<Task> scheduledTasks = new ArrayList<>(schedule.getScheduledTasks());
        scheduledTasks.sort(Comparator.comparing(Task::getName));

        // update the x-axis bounds
        xAxis.setUpperBound((double)schedule.getMakeSpan() + 2);
        xAxis.setTickUnit((double)(schedule.getMakeSpan() + 2) / 10);

        // Add Gantt chart containers
        int i = 0;
        for (Task task : scheduledTasks){
            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>(task.getStartTime(), String.valueOf(task.getProcessorNumber() + 1), new GanttChart.ExtraData(task.getWeight(), containerColourList.get(i), task.getName())));

            ganttChart.getData().add(series);
            setLegend(containerColourList.get(i), task.getName(), task.getStartTime(), task.getFinishTime());
            i++;
        }
    }

    /**
     * Initialize gantt chart container colour
     */
    private void initialiseContainerColour(){
        for (int i = 1; i <= 25; i++) {
            containerColourList.add("container-" + i);
        }
    }

    /**
     * Set the legend of the gantt chart
     */
    private void setLegend(String color, String taskName, int startTime, int finishTime){
        VBox vBox = new VBox();
        StackPane stackPane = new StackPane();
        Rectangle rectangle = new Rectangle(80, 20);
        Label label = new Label();
        Label taskNameLabel = new Label(taskName); // New label
        vBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/rectangle.css")).toExternalForm());
        vBox.setAlignment(Pos.CENTER);
        rectangle.getStyleClass().add(color);
        taskNameLabel.setMinWidth(Region.USE_PREF_SIZE);
        taskNameLabel.setStyle("-fx-font-weight: BOLD; -fx-text-fill: white;");
        label.setText(startTime + " - " + finishTime);
        stackPane.getChildren().addAll(rectangle, taskNameLabel); // Add rectangle and new label to StackPane
        vBox.getChildren().addAll(stackPane, label); // Add StackPane and original label to HBox
        legendHBox.getChildren().add(vBox);
    }

    /**
     * Checks if the current iteration number is within a valid range, updates the user interface accordingly,
     * and ensures that it does not go out of index.
     */
    private void checkNumberValid(){
        if (currentIteration < 0 || currentIteration > maxIteration){
            textFieldNum.setText("Out of Index");
            currentIteration = numCopy;
            invalid.setVisible(true);
            return;
        } else {
            if (invalid.isVisible()){
                invalid.setVisible(false);
            }
        }

        lastIteration.setDisable(currentIteration == 0);
        nextIteration.setDisable(currentIteration == maxIteration);
    }

    /**
     * Handles the action when the "Go To" button is clicked, allowing the user to navigate to a specific iteration
     * by providing an input number in the text field. Validates the input and updates the displayed schedule accordingly.
     */
    @FXML
    public void onClickGotoButton(){
        if (isNumeric(textFieldNum.getText())){
            // keep a copy in case it is invalid
            numCopy = currentIteration;
            currentIteration = Integer.parseInt(textFieldNum.getText());
            checkNumberValid();
            textFieldNum.setText(Integer.toString(currentIteration));
            ISchedule scheduleToDisplay = orderedPartialSchedules.get(currentIteration).getSchedule();
            updateSchedule(scheduleToDisplay);
        }
    }

    /**
     * Handles the action when the "Last Iteration" button is clicked, allowing the user to navigate to the previous iteration.
     * Updates the displayed schedule accordingly.
     *
     * @param actionEvent The event triggered by clicking the "Last Iteration" button.
     */
    @FXML
    public void onClickLastIteration(javafx.event.ActionEvent actionEvent){
        currentIteration--;
        updateSchedule(nextIteration);
    }

    /**
     * Handles the action when the "Next Iteration" button is clicked, allowing the user to navigate to the next iteration.
     * Updates the displayed schedule accordingly.
     *
     * @param actionEvent The event triggered by clicking the "Next Iteration" button.
     */
    @FXML
    public void onClickNextIteration(javafx.event.ActionEvent actionEvent){
        currentIteration++;
        updateSchedule(lastIteration);
    }

    /**
     * Handles the action when the "Finish Iteration" button is clicked, allowing the user to navigate to the final iteration.
     * Disables the "Next Iteration" button, updates the displayed schedule, and marks that the iteration has been used.
     *
     * @param actionEvent The event triggered by clicking the "Finish Iteration" button.
     */
    @FXML
    public void onClickFinishIteration(javafx.event.ActionEvent actionEvent){
        currentIteration = maxIteration;
        nextIteration.setDisable(true);
        lastIteration.setDisable(false);
        checkNumberValid();
        hasUsedIteration = true;
        textFieldNum.setText(Integer.toString(currentIteration));
        ISchedule scheduleToDisplay = orderedPartialSchedules.get(currentIteration).getSchedule();
        updateSchedule(scheduleToDisplay);
    }

    /**
     * Update current schedule based on altering iteration
     * @param iterationButtonToEnable enables a particular iteration button
     */
    public void updateSchedule(Button iterationButtonToEnable) {
        checkNumberValid();
        hasUsedIteration = true;
        textFieldNum.setText(Integer.toString(currentIteration));
        ISchedule scheduleToDisplay = orderedPartialSchedules.get(currentIteration).getSchedule();
        if (iterationButtonToEnable.isDisable()){
            iterationButtonToEnable.setDisable(false);
        }
        if (currentIteration < maxIteration) {
            finishIteration.setDisable(false);
        }
        updateSchedule(scheduleToDisplay);
    }

    /**
     * Handles the action when the "Info" button is clicked, allowing the user to toggle the display of task information on the graph.
     * Updates the user interface accordingly.
     *
     * @param actionEvent The event triggered by clicking the "Info" button.
     */
    @FXML
    public void onClickInfoButton(javafx.event.ActionEvent actionEvent){
        graphUpdate.clickShowInfo(graphStream);
        // could use the graphUpdate.getisShowInfo to set the button to view the thing
    }

    /**
     * Handles the action when the "Move" button is clicked, allowing the user to toggle the ability to move the graph.
     * Updates the user interface accordingly.
     *
     * @param actionEvent The event triggered by clicking the "Move" button.
     */
    @FXML
    public void onClickMoveButton(javafx.event.ActionEvent actionEvent){
         graphUpdate.clickIsMove(viewPanel);
        // could use the graphUpdate.getisMove to set the button to view the thing
    }

    /**
     * Displays a notification in the user interface, indicating the result of the task scheduling process.
     */
    private void showNotification() {
        Notification notification = new Notification();
        notification.setMessage("Task Scheduling has finished for " + io.getInputFileName() + "! " +
                "Time taken was " + executionTime + ".");
        notification.setGraphic(new FontIcon(MaterialDesign.MDI_THUMB_UP));
        notification.getStyleClass().add(Styles.SUCCESS);

        notification.getStyleClass().add(Styles.ELEVATED_1);
        notification.setOnClose(e -> Animations.flash(notification).playFromStart());
        notification.setPrefHeight(Region.USE_PREF_SIZE);
        notification.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(notification, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(notification, new javafx.geometry.Insets(10, 10, 0, 0));
        notification.setOnClose(e -> {
            var out = Animations.slideOutRight(notification, Duration.millis(250));
            out.setOnFinished(f -> {
                Pane parent = (Pane) notification.getParent();
                parent.getChildren().remove(notification);
                notificationContainer.setVisible(false);
            });
            out.playFromStart();
        });
        var in = Animations.slideInLeft(notification, Duration.millis(250));
        if (!notificationContainer.getChildren().contains(notification)) {
            notificationContainer.getChildren().add(notification);
        }
        notificationContainer.setVisible(true);
        in.playFromStart();
    }

    /**
     * Adds drag listeners to a given Node, enabling it to be moved by dragging.
     *
     * @param node The Node to which drag listeners will be added.
     */
    private void addDragListeners(final Node node){
        node.setOnMousePressed(me -> {
            if(me.getButton()!=MouseButton.MIDDLE)
            {
                initialX = me.getSceneX();
                initialY = me.getSceneY();
            }
            else
            {
                node.getScene().getWindow().centerOnScreen();
                initialX = node.getScene().getWindow().getX();
                initialY = node.getScene().getWindow().getY();
            }

        });

        node.setOnMouseDragged(me -> {
            if(me.getButton()!=MouseButton.MIDDLE)
            {
                node.getScene().getWindow().setX( me.getScreenX() - initialX );
                node.getScene().getWindow().setY( me.getScreenY() - initialY);
            }
        });
    }

    /**
     * Checks if the given input is a numeric value (consisting only of digits).
     *
     * @param input The input to be checked for numeric content.
     * @return true if the input is numeric; otherwise, false.
     */
    private boolean isNumeric(String input) {
        return input.matches("\\d*");
    }
}