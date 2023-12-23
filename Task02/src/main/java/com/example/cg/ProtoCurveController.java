
package com.example.cg;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class ProtoCurveController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    ArrayList<Point2D> points = new ArrayList<Point2D>();
    CubicSpline cubicSpline1 = new CubicSpline(new ArrayList<>(), new ArrayList<>());

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        canvas.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY -> handlePrimaryClick(canvas.getGraphicsContext2D(), event);
            }
        });
    }

    private void handlePrimaryClick(GraphicsContext graphicsContext, MouseEvent event) {
        final Point2D clickPoint = new Point2D(event.getX(), event.getY());

        final int POINT_RADIUS = 4;
        graphicsContext.fillOval(
                clickPoint.getX() - POINT_RADIUS, clickPoint.getY() - POINT_RADIUS,
                2 * POINT_RADIUS, 2 * POINT_RADIUS);

        points.add(clickPoint);
        Collections.sort(points, Comparator.comparingDouble(Point2D::getX));

        if (points.size() >= 2) {

            ArrayList<Double> x = new ArrayList<>();
            ArrayList<Double> y = new ArrayList<>();
            for (Point2D point : points) {
                x.add(point.getX());
                y.add(point.getY());
            }

            cubicSpline1 = new CubicSpline(x, y);
            cubicSpline1.solution();

            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            for (int i = 0; i < points.size(); i++) {
                graphicsContext.fillOval(points.get(i).getX() - POINT_RADIUS, points.get(i).getY() - POINT_RADIUS, 2 * POINT_RADIUS, 2 * POINT_RADIUS);
            }

            Double currX = x.get(0);
            Double currY;
            Double prevX = x.get(0);
            Double prevY = cubicSpline1.point(prevX);
            for (double t = x.get(0); t < x.get(x.size() - 1); t += 0.1) {
                Double interpolatedY = cubicSpline1.point(t);
                if (interpolatedY != null) {
                    graphicsContext.strokeLine(prevX, prevY, t, interpolatedY);
                    prevX = t;
                    prevY = interpolatedY;
                }

            }
        }


    }

}

