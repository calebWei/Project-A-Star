package org.softeng306.visualization;

import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The GanttChart class extends JavaFX's XYChart to create a Gantt chart, which is a type of bar chart often used for visualizing project schedules.
 * It represents tasks or activities along a timeline and displays their start and end times as horizontal bars.
 * This class adds functionality for creating Gantt charts, adjusting block heights, and rendering task names.
 * Inspired by: <a href="https://stackoverflow.com/questions/27975898/gantt-chart-from-scratch?answertab=votes#tab-top">...</a>
 *
 * @param <X> The type of the x-axis data.
 * @param <Y> The type of the y-axis data.
 */
public class GanttChart<X,Y> extends XYChart<X,Y> {
    public static class ExtraData {

        private final long length;

        private final String styleClass;

        private final String taskName;

        public ExtraData(long lengthMs, String styleClass, String taskName) {
            super();
            this.length = lengthMs;
            this.styleClass = styleClass;
            this.taskName = taskName;
        }

        public long getLength() {
            return length;
        }

        public String getStyleClass() {
            return styleClass;
        }

        public String getTaskName() {
            return taskName;
        }
    }

    private double blockHeight = 10;

    /**
     * Constructs a GanttChart with default block height.
     *
     * @param xAxis The x-axis.
     * @param yAxis The y-axis.
     */
    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.observableArrayList());
    }

    /**
     * Constructs a GanttChart with the provided x-axis, y-axis, and initial data.
     *
     * @param xAxis The x-axis.
     * @param yAxis The y-axis.
     * @param data  The initial data for the chart.
     */
    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X,Y>> data) {
        super(xAxis, yAxis);
        if (!(xAxis instanceof ValueAxis && yAxis instanceof CategoryAxis)) {
            throw new IllegalArgumentException("Axis type incorrect, X and Y should both be NumberAxis");
        }
        setData(data);
    }

    private static String getStyleClass(Object obj) {
        return ((ExtraData) obj).getStyleClass();
    }

    private static double getLength(Object obj) {
        return ((ExtraData) obj).getLength();
    }

    private static String getTaskName(Object obj) { return ((ExtraData) obj).getTaskName(); }

    @Override protected void layoutPlotChildren() {
        for (int seriesIndex=0; seriesIndex < getData().size(); seriesIndex++) {
            Series<X,Y> series = getData().get(seriesIndex);
            Iterator<Data<X,Y>> iter = getDisplayedDataIterator(series);

            while(iter.hasNext()) {
                Data<X,Y> item = iter.next();
                double x = getXAxis().getDisplayPosition(item.getXValue());
                double y = getYAxis().getDisplayPosition(item.getYValue());
                if (Double.isNaN(x) || Double.isNaN(y)) {
                    continue;
                }

                Node block = item.getNode();
                Rectangle ellipse;

                if ((block instanceof StackPane)) {
                        StackPane region = (StackPane)item.getNode();
                        if (region.getShape() == null) {
                            ellipse = new Rectangle( getLength( item.getExtraValue()), getBlockHeight());
                        } else if (region.getShape() instanceof Rectangle rectangle) {
                            ellipse = rectangle;
                        } else {
                            return;
                        }
                        ellipse.setWidth( getLength( item.getExtraValue()) * ((getXAxis() instanceof NumberAxis numberAxis) ? Math.abs((numberAxis).getScale()) : 1));
                        ellipse.setHeight(getBlockHeight() * ((getYAxis() instanceof NumberAxis numberAxis) ? Math.abs((numberAxis).getScale()) : 1));
                        y -= getBlockHeight() / 2.0;

                        // Get container padding positions
                        double paddingHeight = getBlockHeight() * ((getYAxis() instanceof NumberAxis numberAxis) ? Math.abs((numberAxis).getScale()) : 1);
                        double paddingWidth = getLength( item.getExtraValue()) * ((getXAxis() instanceof NumberAxis numberAxis) ? Math.abs((numberAxis).getScale()) : 1);

                        // initialize block region shape
                        region.setShape(null);
                        region.setShape(ellipse);
                        region.setScaleShape(false);
                        region.setCenterShape(false);
                        region.setCacheShape(false);

                        // Generate task names to the corresponding processor in the center
                        Label taskNameLabel = new Label(getTaskName(item.getExtraValue()));
                        taskNameLabel.setMinWidth(Region.USE_PREF_SIZE);
                        taskNameLabel.setStyle("-fx-font-weight: BOLD; -fx-text-fill: white;");
                        taskNameLabel.setPadding(new Insets(paddingHeight, 0, 0, paddingWidth));
                        region.getChildren().add(taskNameLabel);

                        block.setLayoutX(x);
                        block.setLayoutY(y);
                }
            }
        }
    }

    public double getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight( double blockHeight) {
        this.blockHeight = blockHeight;
    }

    @Override protected void dataItemAdded(Series<X,Y> series, int itemIndex, Data<X,Y> item) {
        Node block = createContainer(item);
        getPlotChildren().add(block);
    }

    @Override protected  void dataItemRemoved(final Data<X,Y> item, final Series<X,Y> series) {
        final Node block = item.getNode();
        getPlotChildren().remove(block);
        removeDataItemFromDisplay(series, item);
    }

    @Override protected void dataItemChanged(Data<X, Y> item) {
        // Why is this empty
    }

    @Override protected  void seriesAdded(Series<X,Y> series, int seriesIndex) {
        for (int j=0; j<series.getData().size(); j++) {
            Data<X,Y> item = series.getData().get(j);
            Node container = createContainer(item);
            getPlotChildren().add(container);
        }
    }

    @Override protected  void seriesRemoved(final Series<X,Y> series) {
        for (XYChart.Data<X,Y> d : series.getData()) {
            final Node container = d.getNode();
            getPlotChildren().remove(container);
        }
        removeSeriesFromDisplay(series);
    }

    private Node createContainer(final Data<X,Y> item) {
        Node container = item.getNode();
        if (container == null) {
            container = new StackPane();
            item.setNode(container);
        }
        container.getStyleClass().add(getStyleClass(item.getExtraValue()));
        return container;
    }

    @Override protected void updateAxisRange() {
        final Axis<X> xa = getXAxis();
        final Axis<Y> ya = getYAxis();
        List<X> xData = null;
        List<Y> yData = null;
        if(xa.isAutoRanging()) xData = new ArrayList<>();
        if(ya.isAutoRanging()) yData = new ArrayList<>();
        if(xData != null || yData != null) {
            for(Series<X,Y> series : getData()) {
                for(Data<X,Y> data: series.getData()) {
                    if(xData != null) {
                        xData.add(data.getXValue());
                        xData.add(xa.toRealValue(xa.toNumericValue(data.getXValue()) + getLength(data.getExtraValue())));
                    }
                    if(yData != null){
                        yData.add(data.getYValue());
                    }
                }
            }
            if(xData != null) xa.invalidateRange(xData);
            if(yData != null) ya.invalidateRange(yData);
        }
    }

}
