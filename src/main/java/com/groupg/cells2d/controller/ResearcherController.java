package com.groupg.cells2d.controller;

import java.util.ArrayList;
import java.util.List;

import com.groupg.cells2d.engine.Propagation;
import com.groupg.cells2d.engine.SimulationEngine;
import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.Grid;
import com.groupg.cells2d.model.board.SEIRData;
import com.groupg.cells2d.model.board.SimulationParams;
import com.groupg.cells2d.model.enums.SimStatus;
import com.groupg.cells2d.stats.Snapshot;
import com.groupg.cells2d.stats.Statistics;
import com.groupg.cells2d.model.board.ParisGridFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class ResearcherController {

    @FXML private StackPane mapPane;
    @FXML private ImageView mapImageView;
    @FXML private Pane districtLayer;
    @FXML private Pane gridLayer;
    @FXML private Label selectedCellLabel;
    @FXML private Label stepLabel;
    @FXML private Label statusLabel;
    @FXML private Button btnPlay;
    @FXML private Button btnPause;
    @FXML private Button btnStop;
    @FXML private Button btnStep;
    @FXML private Slider betaSlider;
    @FXML private Slider gammaSlider;
    @FXML private Label betaLabel;
    @FXML private Label gammaLabel;
    @FXML private Label statsLabel;

    private Grid parisGrid;
    private SimulationEngine engine;
    private Propagation propagation;
    private double canvasWidth;
    private double canvasHeight;
    private final List<Snapshot> statisticHistory = new ArrayList<>();

    @FXML
    public void initialize() {
        parisGrid = ParisGridFactory.createDefaultParisGrid();
        propagation = new Propagation();
        engine = new SimulationEngine(parisGrid, propagation);

        mapImageView.setImage(new Image(getClass().getResourceAsStream(ParisGridFactory.MAP_RESOURCE)));

        // Seed: infect a cell in the center of Paris (arrondissement 1)
        seedInitialInfection();

        mapPane.widthProperty().addListener((obs, o, n) -> drawMap());
        mapPane.heightProperty().addListener((obs, o, n) -> drawMap());

        if (betaSlider != null) {
            betaSlider.setValue(propagation.getParams().getBeta());
            betaSlider.valueProperty().addListener((obs, o, n) -> {
                propagation.getParams().setBeta(n.doubleValue());
                if (betaLabel != null) betaLabel.setText(String.format("β: %.2f", n.doubleValue()));
            });
            if (betaLabel != null) betaLabel.setText(String.format("β: %.2f", propagation.getParams().getBeta()));
        }
        if (gammaSlider != null) {
            gammaSlider.setValue(propagation.getParams().getGamma());
            gammaSlider.valueProperty().addListener((obs, o, n) -> {
                propagation.getParams().setGamma(n.doubleValue());
                if (gammaLabel != null) gammaLabel.setText(String.format("γ: %.2f", n.doubleValue()));
            });
            if (gammaLabel != null) gammaLabel.setText(String.format("γ: %.2f", propagation.getParams().getGamma()));
        }

        updateStatusUI();
        drawMap();
    }

    private void seedInitialInfection() {
        // Find a cell inside Paris and infect it to start the simulation
        for (int row = 0; row < parisGrid.getRows(); row++) {
            for (int col = 0; col < parisGrid.getCols(); col++) {
                Cell cell = parisGrid.getCell(row, col);
                if (cell != null && cell.isInsideParis() && cell.getPopulation() > 0) {
                    int pop = cell.getPopulation();
                    // Start with ~5% infected
                    int infected = Math.max(1, (int)(pop * 1));
                    cell.setSeirData(new SEIRData(pop - infected, 0, infected, 0, 0));
                    cell.updateState(cell.getInfectionRate());
                    return; // seed only one cell
                }
            }
        }
    }

    @FXML
    public void onPlay() {
        if (engine.getStatus() != SimStatus.RUNNING) {
            // Start engine loop in background, refresh UI via Platform.runLater
            engine.play();
            startUIRefreshLoop();
        }
        updateStatusUI();
    }

    @FXML
    public void onPause() {
        engine.pause();
        updateStatusUI();
    }

    @FXML
    public void onStop() {
        engine.stop();
        // Reset grid
        parisGrid = ParisGridFactory.createDefaultParisGrid();
        propagation = new Propagation(new SimulationParams(
            propagation.getParams().getBeta(),
            propagation.getParams().getSigma(),
            propagation.getParams().getGamma(),
            propagation.getParams().getPropagationRate(),
            propagation.getParams().getMortalityRate()
        ));
        engine = new SimulationEngine(parisGrid, propagation);
        seedInitialInfection();
        updateStatusUI();
        drawMap();
    }

    @FXML
    public void onStep() {
        if (engine.getStatus() != SimStatus.RUNNING) {
            engine.step();
            Snapshot stats =
                Statistics.compute(parisGrid, engine.getStepCount());

            statisticHistory.add(stats);
            updateStatisticsUI(stats);
            updateStatusUI();
            drawGrid();
        }
    }

    
    private void startUIRefreshLoop() {
        Thread refreshThread = new Thread(() -> {
            while (engine.getStatus() == SimStatus.RUNNING) {
                Platform.runLater(() -> {
                    drawGrid();
                    updateStatusUI();
                });
                try { Thread.sleep(520); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            // Final refresh after stop/pause
            Platform.runLater(() -> { drawGrid(); updateStatusUI(); });
        });
        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    private void updateStatusUI() {
        if (stepLabel != null) stepLabel.setText("Étape: " + engine.getStepCount());
        if (statusLabel != null) statusLabel.setText("Statut: " + engine.getStatus());
    }

    private void drawMap() {
        if (mapPane == null || mapImageView == null || districtLayer == null
                || gridLayer == null || parisGrid == null) return;
        if (mapPane.getWidth() <= 0 || mapPane.getHeight() <= 0) return;

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
                if (cell == null || !cell.isInsideParis()) continue;

                Rectangle rect = new Rectangle(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                rect.setStroke(Color.rgb(20, 20, 20, 0.22));
                rect.setStrokeWidth(0.35);
                rect.setFill(getColorFor(cell));
                rect.setOnMouseClicked(event -> selectCell(cell, rect));
                gridLayer.getChildren().add(rect);
            }
        }

        drawDistrictBordersOnCellEdges(cellWidth, cellHeight);
    }

    private void drawDistrictBordersOnCellEdges(double cellWidth, double cellHeight) {
        for (int row = 0; row < parisGrid.getRows(); row++) {
            for (int col = 0; col < parisGrid.getCols(); col++) {
                Cell cell = parisGrid.getCell(row, col);
                if (cell == null || !cell.isInsideParis()) continue;

                if (isDifferentDistrict(cell, row, col + 1)) addBorderLine((col+1)*cellWidth, row*cellHeight, (col+1)*cellWidth, (row+1)*cellHeight);
                if (isDifferentDistrict(cell, row + 1, col)) addBorderLine(col*cellWidth, (row+1)*cellHeight, (col+1)*cellWidth, (row+1)*cellHeight);
                if (isDifferentDistrict(cell, row, col - 1)) addBorderLine(col*cellWidth, row*cellHeight, col*cellWidth, (row+1)*cellHeight);
                if (isDifferentDistrict(cell, row - 1, col)) addBorderLine(col*cellWidth, row*cellHeight, (col+1)*cellWidth, row*cellHeight);
            }
        }
    }

    private boolean isDifferentDistrict(Cell cell, int nRow, int nCol) {
        if (nRow < 0 || nRow >= parisGrid.getRows() || nCol < 0 || nCol >= parisGrid.getCols()) return true;
        Cell neighbor = parisGrid.getCell(nRow, nCol);
        if (neighbor == null || !neighbor.isInsideParis()) return true;
        String cd = cell.getDistrictId(), nd = neighbor.getDistrictId();
        return cd != null && nd != null && !cd.equals(nd);
    }

    private void addBorderLine(double x1, double y1, double x2, double y2) {
        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(Color.rgb(0, 0, 0, 0.85));
        line.setStrokeWidth(1.5);
        line.setMouseTransparent(true);
        gridLayer.getChildren().add(line);
    }

    private void selectCell(Cell cell, Rectangle rect) {
        if (!cell.isInsideParis()) return;
        SEIRData d = cell.getSeirData();
        selectedCellLabel.setText(
            cell.getCellId() + " | " + cell.getDistrictName()
            + " | état: " + cell.getState()
            + " | pop: " + cell.getPopulation()
            + String.format(" | S:%.0f E:%.0f I:%.0f R:%.0f D:%.0f",
                d.getSusceptible(), d.getExposed(), d.getInfected(), d.getRecovered(), d.getDead())
        );
    }

    private Color getColorFor(Cell cell) {
        switch (cell.getState()) {
            case HEALTHY:  return Color.rgb(89, 180, 90, 0.28);
            case PARTIAL:  return Color.rgb(255, 193, 7, 0.60);
            case INFECTED: return Color.rgb(220, 53, 69, 0.72);
            case CRITICAL: return Color.rgb(33, 150, 243, 0.65);
            default:       return Color.rgb(180, 180, 180, 0.30);
        }
    }

    private void updateStatisticsUI(Snapshot stats) {
    statsLabel.setText(
            "Healthy : " + stats.getHealthyCells()
            + "\nPartial : " + stats.getPartialCells()
            + "\nInfected : " + stats.getInfectedCells()
            + "\nCritical : " + stats.getCriticalCells()
            + "\nTotal : " + stats.getTotalCells()
    );
}
}
