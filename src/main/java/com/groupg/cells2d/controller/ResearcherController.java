package com.groupg.cells2d.controller;

import java.util.ArrayList;
import java.util.List;

import com.groupg.cells2d.engine.Propagation;
import com.groupg.cells2d.engine.SimulationEngine;
import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.Grid;
import com.groupg.cells2d.model.board.SEIRData;
import com.groupg.cells2d.model.board.SimulationParams;
import com.groupg.cells2d.model.board.ParisGridFactory;
import com.groupg.cells2d.model.enums.CellState;
import com.groupg.cells2d.model.enums.SimStatus;
import com.groupg.cells2d.stats.Snapshot;
import com.groupg.cells2d.stats.Statistics;
import com.groupg.cells2d.model.board.ParisGridFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ResearcherController {

    // --- Map ---
    @FXML private StackPane mapPane;
    @FXML private ImageView mapImageView;
    @FXML private Pane districtLayer;
    @FXML private Pane gridLayer;

    // --- Modes ---
    @FXML private RadioButton cursorRadio;
    @FXML private RadioButton brushRadio;
    @FXML private RadioButton bucketRadio;

    // --- Controls ---
    @FXML private Button btnPlay;
    @FXML private Button btnPause;
    @FXML private Button btnStop;
    @FXML private Button btnStep;

    // --- Status ---
    @FXML private Label stepLabel;
    @FXML private Label statusLabel;
    @FXML private Label selectedCellLabel;

    // --- Sliders ---
    @FXML private Slider betaSlider;
    @FXML private Slider gammaSlider;
    @FXML private Slider sigmaSlider;
    @FXML private Slider xiSlider;
    @FXML private Slider mortalitySlider;
    @FXML private Slider propagationSlider;
    @FXML private Label mortalityLabel;
    @FXML private Label propagationLabel;
    @FXML private Label betaLabel;
    @FXML private Label gammaLabel;
    @FXML private Label sigmaLabel;
    @FXML private Label xiLabel;
    @FXML private Label statsLabel;

    // --- State ---
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

        bindSliders();

        mapPane.widthProperty().addListener((obs, o, n) -> drawMap());
        mapPane.heightProperty().addListener((obs, o, n) -> drawMap());

        drawMap();
        updateStatusUI();
    }

    // -------------------------------------------------------------------------
    // Sliders
    // -------------------------------------------------------------------------

    private void bindSliders() {
        betaSlider.setValue(propagation.getParams().getBeta());
        betaSlider.valueProperty().addListener((obs, o, n) -> {
            propagation.getParams().setBeta(n.doubleValue());
            betaLabel.setText(String.format("β (transmission): %.2f", n.doubleValue()));
        });
        betaLabel.setText(String.format("β (transmission): %.2f", propagation.getParams().getBeta()));

        gammaSlider.setValue(propagation.getParams().getGamma());
        gammaSlider.valueProperty().addListener((obs, o, n) -> {
            propagation.getParams().setGamma(n.doubleValue());
            gammaLabel.setText(String.format("γ (guérison): %.2f", n.doubleValue()));
        });
        gammaLabel.setText(String.format("γ (guérison): %.2f", propagation.getParams().getGamma()));

        sigmaSlider.setValue(propagation.getParams().getSigma());
        sigmaSlider.valueProperty().addListener((obs, o, n) -> {
            propagation.getParams().setSigma(n.doubleValue());
            sigmaLabel.setText(String.format("σ (incubation): %.2f", n.doubleValue()));
        });
        sigmaLabel.setText(String.format("σ (incubation): %.2f", propagation.getParams().getSigma()));

        xiSlider.setValue(propagation.getParams().getXi());
        xiSlider.valueProperty().addListener((obs, o, n) -> {
            propagation.getParams().setXi(n.doubleValue());
            xiLabel.setText(String.format("ξ (déclin d'immunité, SEIRS): %.2f", n.doubleValue()));
        });
        xiLabel.setText(String.format("ξ (déclin d'immunité, SEIRS): %.2f", propagation.getParams().getXi()));

        mortalitySlider.setValue(propagation.getParams().getMortalityRate());
        mortalitySlider.valueProperty().addListener((obs,o,n)->{
            propagation.getParams().setMortalityRate(n.doubleValue());
            mortalityLabel.setText(String.format("Taux de mortalité: %.2f", n.doubleValue()));
        });
        mortalityLabel.setText(String.format("Taux de mortalité: %.2f",propagation.getParams().getMortalityRate()));

        propagationSlider.setValue(propagation.getParams().getPropagationRate());
        propagationSlider.valueProperty().addListener((obs,o,n)->{
            propagation.getParams().setPropagationRate(n.doubleValue());
            propagationLabel.setText(String.format("Taux de propagation: %.2f", n.doubleValue()));
        });
        propagationLabel.setText(String.format("Taux de propagation: %.2f",propagation.getParams().getPropagationRate()));
    }

    // -------------------------------------------------------------------------
    // Simulation controls
    // -------------------------------------------------------------------------

    @FXML public void onPlay() {
        if (engine.getStatus() != SimStatus.RUNNING) {
            engine.play();
            startUIRefreshLoop();
        }
        updateStatusUI();
    }

    @FXML public void onPause() {
        engine.pause();
        updateStatusUI();
    }

    @FXML public void onStop() {
        engine.stop();
        parisGrid = ParisGridFactory.createDefaultParisGrid();
        propagation = new Propagation(new SimulationParams(
            propagation.getParams().getBeta(),
            propagation.getParams().getSigma(),
            propagation.getParams().getGamma(),
            propagation.getParams().getPropagationRate(),
            propagation.getParams().getMortalityRate()
        ));
        engine = new SimulationEngine(parisGrid, propagation);
        updateStatusUI();
        drawMap();
    }

    @FXML public void onStep() {
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
        Thread t = new Thread(() -> {
            while (engine.getStatus() == SimStatus.RUNNING) {
                Platform.runLater(() -> { drawGrid(); updateStatusUI(); });
                Platform.runLater(() -> {
                    Snapshot stats = Statistics.compute(parisGrid, engine.getStepCount());

                    statisticHistory.add(stats);
                    updateStatisticsUI(stats);

                    drawGrid();
                    updateStatusUI();
                });
                try { Thread.sleep(520); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            Platform.runLater(() -> { drawGrid(); updateStatusUI(); });
            // Final refresh after stop/pause
            Platform.runLater(() -> {
                Snapshot stats = Statistics.compute(parisGrid, engine.getStepCount());
                statisticHistory.add(stats);
                updateStatisticsUI(stats);
                drawGrid(); updateStatusUI(); });
        });
        t.setDaemon(true);
        t.start();
    }

    private void updateStatusUI() {
        stepLabel.setText("Étape: " + engine.getStepCount());
        statusLabel.setText("Statut: " + engine.getStatus());
    }

    // -------------------------------------------------------------------------
    // Drawing
    // -------------------------------------------------------------------------

    private void drawMap() {
        if (mapPane == null || mapPane.getWidth() <= 0 || mapPane.getHeight() <= 0) return;

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

        double cellWidth  = canvasWidth  / parisGrid.getCols();
        double cellHeight = canvasHeight / parisGrid.getRows();

        for (int row = 0; row < parisGrid.getRows(); row++) {
            for (int col = 0; col < parisGrid.getCols(); col++) {
                Cell cell = parisGrid.getCell(row, col);
                if (cell == null || !cell.isInsideParis()) continue;

                Rectangle rect = new Rectangle(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                rect.setStroke(Color.rgb(20, 20, 20, 0.22));
                rect.setStrokeWidth(0.35);
                rect.setFill(getColorFor(cell));
                if(!cell.isAlive()){
                    rect.setStrokeWidth(3);
                    rect.setStroke(Color.rgb(255, 0, 0, 0.85));
                }

                // --- Cursor : clic simple pour sélectionner et afficher les infos ---
                rect.setOnMouseClicked(event -> {
                    if (cursorRadio.isSelected()) {
                        selectCell(cell, rect);
                    }
                });

                // --- Brush : peint la cellule au clic ET en faisant glisser ---
                rect.setOnMousePressed(event -> {
                    if (brushRadio.isSelected()) {
                        paintCell(cell, rect);
                    }
                });
                // --- Bucket / Cursor ---
                rect.setOnMouseClicked(event -> {
                    if (cursorRadio.isSelected()) {
                        selectCell(cell, rect);
                    } else if (bucketRadio.isSelected()) {
                        bucketFill(cell);
                    }
                });

                gridLayer.getChildren().add(rect);
            }
        }

        drawDistrictBordersOnCellEdges(cellWidth, cellHeight);
    }

    // -------------------------------------------------------------------------
    // Modes : Cursor / Brush / Bucket
    // -------------------------------------------------------------------------

    /** Cursor : affiche les infos SEIR de la cellule dans le panel gauche */
    private void selectCell(Cell cell, Rectangle rect) {
        SEIRData d = cell.getSeirData();
        selectedCellLabel.setText(
            cell.getCellId() + " | " + cell.getDistrictName()
            + " | état: " + cell.getState()
            + " | pop: " + cell.getPopulation()
            + String.format(" | S:%.0f E:%.0f I:%.0f R:%.0f D:%.0f",
                d.getSusceptible(), d.getExposed(), d.getInfected(), d.getRecovered(), d.getDead())
        );
    }

    /** Brush : cycle HEALTHY→PARTIAL→INFECTED→CRITICAL sur la cellule sous le curseur */
    private void paintCell(Cell cell, Rectangle rect) {
        cell.setState(nextState(cell.getState()));
        syncSeirToState(cell);
        rect.setFill(getColorFor(cell));
    }

    /** Bucket : applique le prochain état à toutes les cellules du même arrondissement */
    private void bucketFill(Cell clicked) {
        String districtId = clicked.getDistrictId();
        if (districtId == null) return;
        CellState target = nextState(clicked.getState());
        for (int row = 0; row < parisGrid.getRows(); row++) {
            for (int col = 0; col < parisGrid.getCols(); col++) {
                Cell cell = parisGrid.getCell(row, col);
                if (cell != null && cell.isInsideParis() && districtId.equals(cell.getDistrictId())) {
                    cell.setState(target);
                    syncSeirToState(cell);
                }
            }
        }
        drawGrid(); // redessine tout l'arrondissement d'un coup
    }

    /**
     * Synchronise les données SEIR avec l'état visuel posé manuellement.
     * Permet que la simulation démarre cohérente même après un dessin.
     */
    private void syncSeirToState(Cell cell) {
        int pop = cell.getPopulation();
        if (pop <= 0) return;
        switch (cell.getState()) {
            case HEALTHY:  cell.setSeirData(new SEIRData(pop, 0, 0, 0, 0)); break;
            case PARTIAL:  cell.setSeirData(new SEIRData((int)(pop*0.8), (int)(pop*0.2), 0, 0, 0)); break;
            case INFECTED: cell.setSeirData(new SEIRData((int)(pop*0.6), 0, (int)(pop*0.4), 0, 0)); break;
            case CRITICAL: cell.setSeirData(new SEIRData((int)(pop*0.3), 0, (int)(pop*0.6), (int)(pop*0.1), 0)); break;
        }
    }

    // -------------------------------------------------------------------------
    // Borders
    // -------------------------------------------------------------------------

    private void drawDistrictBordersOnCellEdges(double cellWidth, double cellHeight) {
        for (int row = 0; row < parisGrid.getRows(); row++) {
            for (int col = 0; col < parisGrid.getCols(); col++) {
                Cell cell = parisGrid.getCell(row, col);
                if (cell == null || !cell.isInsideParis()) continue;
                if (isDifferentDistrict(cell, row, col + 1)) addBorderLine((col+1)*cellWidth, row*cellHeight,    (col+1)*cellWidth, (row+1)*cellHeight);
                if (isDifferentDistrict(cell, row + 1, col)) addBorderLine(col*cellWidth,     (row+1)*cellHeight, (col+1)*cellWidth, (row+1)*cellHeight);
                if (isDifferentDistrict(cell, row, col - 1)) addBorderLine(col*cellWidth,     row*cellHeight,     col*cellWidth,     (row+1)*cellHeight);
                if (isDifferentDistrict(cell, row - 1, col)) addBorderLine(col*cellWidth,     row*cellHeight,     (col+1)*cellWidth, row*cellHeight);
            }
        }
    }

    private boolean isDifferentDistrict(Cell cell, int nRow, int nCol) {
        if (nRow < 0 || nRow >= parisGrid.getRows() || nCol < 0 || nCol >= parisGrid.getCols()) return true;
        Cell n = parisGrid.getCell(nRow, nCol);
        if (n == null || !n.isInsideParis()) return true;
        String cd = cell.getDistrictId(), nd = n.getDistrictId();
        return cd != null && nd != null && !cd.equals(nd);
    }

    private void addBorderLine(double x1, double y1, double x2, double y2) {
        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(Color.rgb(0, 0, 0, 0.85));
        line.setStrokeWidth(1.5);
        line.setMouseTransparent(true);
        gridLayer.getChildren().add(line);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private CellState nextState(CellState state) {
        if (state == CellState.HEALTHY)  return CellState.PARTIAL;
        if (state == CellState.PARTIAL)  return CellState.INFECTED;
        if (state == CellState.INFECTED) return CellState.CRITICAL;
        return CellState.HEALTHY;
    }

    private Color getColorFor(Cell cell) {
        switch (cell.getState()) {
            case HEALTHY:  return Color.rgb(89,  180, 90,  0.28);
            case PARTIAL:  return Color.rgb(255, 193, 7,   0.60);
            case INFECTED: return Color.rgb(220, 53,  69,  0.72);
            case CRITICAL: return Color.rgb(0,   0,   0,   0.60);
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

    @FXML
public void onShowStatisticsCharts() {
    Stage stage = new Stage();
        stage.setTitle("Graphiques statistiques");

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Step");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nombre de cellules");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Évolution des cellules");

        CheckBox healthyCheck = new CheckBox("Healthy");
        CheckBox partialCheck = new CheckBox("Partial");
        CheckBox infectedCheck = new CheckBox("Infected");
        CheckBox criticalCheck = new CheckBox("Critical");

        healthyCheck.setSelected(true);
        partialCheck.setSelected(true);
        infectedCheck.setSelected(true);
        criticalCheck.setSelected(true);

        Button updateButton = new Button("Update chart");

        updateButton.setOnAction(event -> {
            chart.getData().clear();

            if (healthyCheck.isSelected()) {
                chart.getData().add(createSeries("Healthy", "healthy"));
            }
            if (partialCheck.isSelected()) {
                chart.getData().add(createSeries("Partial", "partial"));
            }
            if (infectedCheck.isSelected()) {
                chart.getData().add(createSeries("Infected", "infected"));
            }
            if (criticalCheck.isSelected()) {
                chart.getData().add(createSeries("Critical", "critical"));
            }
        });

        updateButton.fire();

        HBox checkBoxBox = new HBox(10, healthyCheck, partialCheck, infectedCheck, criticalCheck, updateButton);
        VBox root = new VBox(10, checkBoxBox, chart);

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.show();
    }

    private XYChart.Series<Number, Number> createSeries(String name, String type) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);

        for (Snapshot snapshot : statisticHistory) {
            int value = 0;

            switch (type) {
                case "healthy":
                    value = snapshot.getHealthyCells();
                    break;
                case "partial":
                    value = snapshot.getPartialCells();
                    break;
                case "infected":
                    value = snapshot.getInfectedCells();
                    break;
                case "critical":
                    value = snapshot.getCriticalCells();
                    break;
            }

            series.getData().add(new XYChart.Data<>(snapshot.getStep(), value));
        }

        return series;
    }

}
