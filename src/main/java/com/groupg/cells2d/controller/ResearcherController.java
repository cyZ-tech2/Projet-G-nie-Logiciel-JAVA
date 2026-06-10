package com.groupg.cells2d.controller;

import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.Grid;
import com.groupg.cells2d.model.enums.CellState;
import com.groupg.cells2d.model.board.ParisGridFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class ResearcherController {

    @FXML
    private StackPane mapPane;

    @FXML
    private ImageView mapImageView;

    @FXML
    private Pane districtLayer;

    @FXML
    private Pane gridLayer;

    @FXML
    private Label selectedCellLabel;

    private Grid parisGrid;
    private double canvasWidth;
    private double canvasHeight;

    @FXML
    public void initialize() {
        parisGrid = ParisGridFactory.createDefaultParisGrid();
        mapImageView.setImage(new Image(getClass().getResourceAsStream(ParisGridFactory.MAP_RESOURCE)));

        mapPane.widthProperty().addListener((obs, oldValue, newValue) -> drawMap());
        mapPane.heightProperty().addListener((obs, oldValue, newValue) -> drawMap());

        drawMap();
    }

    private void drawMap() {
        if (mapPane == null || mapImageView == null || districtLayer == null || gridLayer == null || parisGrid == null) {
            return;
        }
        if (mapPane.getWidth() <= 0 || mapPane.getHeight() <= 0) {
            return;
        }

        double ratio = ParisGridFactory.MAP_WIDTH / ParisGridFactory.MAP_HEIGHT;
        canvasWidth = mapPane.getWidth();
        canvasHeight = canvasWidth / ratio;
        if (canvasHeight > mapPane.getHeight()) {
            canvasHeight = mapPane.getHeight();
            canvasWidth = canvasHeight * ratio;
        }

        mapImageView.setFitWidth(canvasWidth);
        mapImageView.setFitHeight(canvasHeight);

        districtLayer.setPrefSize(canvasWidth, canvasHeight);
        districtLayer.setMaxSize(canvasWidth, canvasHeight);
        districtLayer.setMinSize(canvasWidth, canvasHeight);

        gridLayer.setPrefSize(canvasWidth, canvasHeight);
        gridLayer.setMaxSize(canvasWidth, canvasHeight);
        gridLayer.setMinSize(canvasWidth, canvasHeight);

        drawGrid();
    }

    private void drawGrid() {
        gridLayer.getChildren().clear();

        double cellWidth = canvasWidth / parisGrid.getCols();
        double cellHeight = canvasHeight / parisGrid.getRows();

        for (int row = 0; row < parisGrid.getRows(); row++) {
            for (int col = 0; col < parisGrid.getCols(); col++) {
                Cell cell = parisGrid.getCell(row, col);
                if (cell == null || !cell.isInsideParis()) {
                    continue;
                }

                Rectangle rectangle = new Rectangle(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                rectangle.setStroke(Color.rgb(20, 20, 20, 0.22));
                rectangle.setStrokeWidth(0.35);
                rectangle.setFill(getColorFor(cell));

                rectangle.setOnMouseClicked(event -> selectCell(cell, rectangle));
                gridLayer.getChildren().add(rectangle);
            }
        }

        drawDistrictBordersOnCellEdges(cellWidth, cellHeight);
    }

    /**
     * Borders are drawn only on cell edges. So the visible arrondissement line
     * never crosses the middle of a case, and one case belongs to one district.
     */
    private void drawDistrictBordersOnCellEdges(double cellWidth, double cellHeight) {
        for (int row = 0; row < parisGrid.getRows(); row++) {
            for (int col = 0; col < parisGrid.getCols(); col++) {
                Cell cell = parisGrid.getCell(row, col);
                if (cell == null || !cell.isInsideParis()) {
                    continue;
                }

                if (isDifferentDistrict(cell, row, col + 1)) {
                    addBorderLine((col + 1) * cellWidth, row * cellHeight,
                            (col + 1) * cellWidth, (row + 1) * cellHeight);
                }
                if (isDifferentDistrict(cell, row + 1, col)) {
                    addBorderLine(col * cellWidth, (row + 1) * cellHeight,
                            (col + 1) * cellWidth, (row + 1) * cellHeight);
                }
                if (isDifferentDistrict(cell, row, col - 1)) {
                    addBorderLine(col * cellWidth, row * cellHeight,
                            col * cellWidth, (row + 1) * cellHeight);
                }
                if (isDifferentDistrict(cell, row - 1, col)) {
                    addBorderLine(col * cellWidth, row * cellHeight,
                            (col + 1) * cellWidth, row * cellHeight);
                }
            }
        }
    }

    private boolean isDifferentDistrict(Cell cell, int neighborRow, int neighborCol) {
        if (neighborRow < 0 || neighborRow >= parisGrid.getRows()
                || neighborCol < 0 || neighborCol >= parisGrid.getCols()) {
            return true;
        }

        Cell neighbor = parisGrid.getCell(neighborRow, neighborCol);
        if (neighbor == null || !neighbor.isInsideParis()) {
            return true;
        }

        String currentDistrict = cell.getDistrictId();
        String neighborDistrict = neighbor.getDistrictId();
        return currentDistrict != null && neighborDistrict != null && !currentDistrict.equals(neighborDistrict);
    }

    private void addBorderLine(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.rgb(0, 0, 0, 0.85));
        line.setStrokeWidth(1.5);
        line.setMouseTransparent(true);
        gridLayer.getChildren().add(line);
    }

    private void selectCell(Cell cell, Rectangle rectangle) {
        if (!cell.isInsideParis()) {
            return;
        }

        cell.setState(nextState(cell.getState()));
        rectangle.setFill(getColorFor(cell));

        selectedCellLabel.setText(
                cell.getCellId()
                        + " | " + cell.getDistrictName()
                        + " | état: " + cell.getState()
                        + " | population: " + cell.getPopulation()
        );
    }

    private CellState nextState(CellState state) {
        if (state == CellState.HEALTHY) return CellState.PARTIAL;
        if (state == CellState.PARTIAL) return CellState.INFECTED;
        if (state == CellState.INFECTED) return CellState.CRITICAL;
        return CellState.HEALTHY;
    }

    private Color getColorFor(Cell cell) {
        if (cell.getState() == CellState.HEALTHY) {
            return Color.rgb(89, 180, 90, 0.28);
        }
        if (cell.getState() == CellState.PARTIAL) {
            return Color.rgb(255, 193, 7, 0.60);
        }
        if (cell.getState() == CellState.INFECTED) {
            return Color.rgb(220, 53, 69, 0.72);
        }
        if (cell.getState() == CellState.CRITICAL) {
            return Color.rgb(33, 150, 243, 0.65);
        }
        return Color.rgb(180, 180, 180, 0.30);
    }
}
