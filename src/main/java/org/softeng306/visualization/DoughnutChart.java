package org.softeng306.visualization;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

/**
 * The DoughnutChart class is an extension of JavaFX's PieChart, designed to create a doughnut chart.
 * It includes functionality for adding and clearing pie chart data and rendering an inner circle within the chart.
 * This inner circle provides the doughnut chart appearance.
 * Inspired by: <a href="https://stackoverflow.com/questions/24121580/can-piechart-from-javafx-be-displayed-as-a-doughnut">an-piechart-from-javafx-be-displayed-as-a-doughnut</a>
 */
public class DoughnutChart extends PieChart {
    private Circle innerCircle;

    /**
     * Constructs a DoughnutChart with no initial pie data and adds the inner circle for the doughnut effect.
     */
    public DoughnutChart() {
        makeInnerCircle();
    }

    /**
     * Creates the inner circle for the doughnut chart and sets its properties.
     */
    private void makeInnerCircle() {
        innerCircle = new Circle();
        innerCircle.setStrokeWidth(3);
        innerCircle.getStyleClass().add("doughnut");
    }
    @Override
    protected void layoutChartChildren(double top, double left, double contentWidth, double contentHeight) {
        super.layoutChartChildren(top, left, contentWidth, contentHeight);
        addInnerCircleIfNotPresent();
        updateInnerCircleLayout();
    }

    /**
     * Adds the inner circle to the chart if it's not already present.
     */
    private void addInnerCircleIfNotPresent() {
        if (!getData().isEmpty()) {
            Node pie = getData().get(0).getNode();
            if (pie.getParent() instanceof Pane parent && (!parent.getChildren().contains(innerCircle))) {
                    parent.getChildren().add(innerCircle);

            }
        }
    }

    /**
     * Updates the layout of the inner circle to fit within the pie chart.
     */
    private void updateInnerCircleLayout() {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        for (PieChart.Data data: getData()) {
            Node node = data.getNode();

            Bounds bounds = node.getBoundsInParent();
            if (bounds.getMinX() < minX) {
                minX = bounds.getMinX();
            }
            if (bounds.getMinY() < minY) {
                minY = bounds.getMinY();
            }
            if (bounds.getMaxX() > maxX) {
                maxX = bounds.getMaxX();
            }
            if (bounds.getMaxY() > maxY) {
                maxY = bounds.getMaxY();
            }
        }

        innerCircle.setCenterX(minX + (maxX - minX) / 2);
        innerCircle.setCenterY(minY + (maxY - minY) / 2);

        innerCircle.setRadius((maxX - minX) / 4);
    }

    /**
     * Adds the specified PieChart.Data elements to the chart.
     *
     * @param elements The PieChart.Data elements to add to the chart.
     */
    public void addPieChartData(PieChart.Data... elements) {
        if (elements != null) {
            getData().addAll(elements);
        }
    }

    /**
     * Clears all pie chart data from the chart.
     */
    public void clearPieChartData() {
        getData().clear();
    }
}