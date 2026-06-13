package com.groupg.cells2d.controller;

import com.groupg.cells2d.engine.SimulationEngine;
import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.Grid;
import com.groupg.cells2d.model.board.ParisGridFactory;
import com.groupg.cells2d.model.enums.SimStatus;
import com.groupg.cells2d.stats.DistrictSnapshot;
import com.groupg.cells2d.stats.Snapshot;
import com.groupg.cells2d.stats.Statistics;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base controller shared by ResearcherController and DoctorInterfaceController.
 * Handles map rendering, simulation loop, statistics, and chart display.
 */
public abstract class AbstractSimController {

    // --- Map layers (injected by sub-controllers via FXML) ---
    @FXML protected StackPane mapPane;
    @FXML protected ImageView mapImageView;
    @FXML protected Pane      districtLayer;
    @FXML protected Pane      gridLayer;

    // --- Status labels ---
    @FXML protected Label stepLabel;
    @FXML protected Label statusLabel;

    // --- Shared state ---
    protected Grid             parisGrid;
    protected SimulationEngine engine;
    protected double           canvasWidth;
    protected double           canvasHeight;

    // --- Statistics ---
    protected final List<Snapshot>                        statisticHistory = new ArrayList<>();
    protected final List<Map<String, DistrictSnapshot>>   districtHistory  = new ArrayList<>();
    protected int lastRecordedStep = -1;

    // -------------------------------------------------------------------------
    // Abstract methods — implemented differently in each sub-class
    // -------------------------------------------------------------------------

    /** Called after each click on a map cell. */
    protected abstract void onCellClicked(Cell cell);

    /** Allows each sub-class to apply custom cell decorations (highlight, border, etc.). */
    protected abstract Color getCellStrokeColor(Cell cell);
    /** Returns the stroke width to apply to the given cell's rectangle. */
    protected abstract double getCellStrokeWidth(Cell cell);

    // -------------------------------------------------------------------------
    // Simulation controls (shared)
    // -------------------------------------------------------------------------

    /** Path to the about HTML file — implemented by each sub-controller. */
    protected abstract String getAboutResourcePath();

    /**
     * Opens the "About" HTML page in a modal WebView window.
     * The page path is provided by each sub-controller.
     */
    @FXML
    public void onAbout() {
        try {
            javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
            webView.getEngine().load(
                getClass().getResource(getAboutResourcePath()).toExternalForm());
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("About Cells2D");
            stage.setScene(new javafx.scene.Scene(webView, 700, 500));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Pauses the simulation and closes the main application window.
     */
    @FXML
    public void onCloseApplication() {
        try {
            engine.pause();
            mapPane.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Pauses the simulation and navigates back to the login screen.
     */
    @FXML
    public void onLogout() {
        try {
            engine.pause();
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/com/groupg/cells2d/view/login.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) mapPane.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the simulation if it is not already running,
     * then launches the UI refresh loop.
     */
    @FXML
    public void onPlay() {
        if (engine.getStatus() != SimStatus.RUNNING) {
            engine.play();
            startUIRefreshLoop();
        }
        updateStatusUI();
    }

    /**
     * Pauses the simulation and refreshes the status labels.
     */
    @FXML
    public void onPause() {
        engine.pause();
        updateStatusUI();
    }

    /**
     * Spawns a daemon thread that refreshes the map, statistics, and status
     * labels every ~520 ms while the simulation is running.
     */
    protected void startUIRefreshLoop() {
        Thread t = new Thread(() -> {
            while (engine.getStatus() == SimStatus.RUNNING) {
                Platform.runLater(() -> {
                    updateStatistics();
                    drawGrid();
                    updateStatusUI();
                });
                try { Thread.sleep(520); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            Platform.runLater(() -> {
                updateStatistics();
                drawGrid();
                updateStatusUI();
            });
        });
        t.setDaemon(true);
        t.start();
    }

    /**
     * Updates the step and status labels with the current engine state.
     * Safe to call from any thread (null-checks both labels).
     */
    protected void updateStatusUI() {
        if (stepLabel   != null) stepLabel.setText("Étape : "  + engine.getStepCount());
        if (statusLabel != null) statusLabel.setText("Statut : " + engine.getStatus());
    }

    // -------------------------------------------------------------------------
    // Map rendering
    // -------------------------------------------------------------------------

    /**
     * Computes the canvas dimensions preserving the Paris map aspect ratio,
     * resizes all overlay layers accordingly, and redraws the grid.
     */
    protected void drawMap() {
        if (mapPane == null || mapPane.getWidth() <= 0 || mapPane.getHeight() <= 0) return;

        double ratio = ParisGridFactory.MAP_WIDTH / ParisGridFactory.MAP_HEIGHT;
        canvasWidth  = mapPane.getWidth();
        canvasHeight = canvasWidth / ratio;
        if (canvasHeight > mapPane.getHeight()) {
            canvasHeight = mapPane.getHeight();
            canvasWidth  = canvasHeight * ratio;
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

    /**
     * Clears and redraws every Paris cell as a coloured rectangle,
     * then overlays the district borders.
     * Sub-classes may override this to add extra interaction handlers.
     */
    protected void drawGrid() {
        gridLayer.getChildren().clear();

        double cellWidth  = canvasWidth  / parisGrid.getCols();
        double cellHeight = canvasHeight / parisGrid.getRows();

        for (int row = 0; row < parisGrid.getRows(); row++) {
            for (int col = 0; col < parisGrid.getCols(); col++) {
                Cell cell = parisGrid.getCell(row, col);
                if (cell == null || !cell.isInsideParis()) continue;

                Rectangle rect = new Rectangle(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                rect.setFill(getFillColorFor(cell));
                rect.setStroke(getCellStrokeColor(cell));
                rect.setStrokeWidth(getCellStrokeWidth(cell));

                rect.setOnMouseClicked(event -> onCellClicked(cell));
                gridLayer.getChildren().add(rect);
            }
        }

        drawDistrictBorders(cellWidth, cellHeight);
    }

    /**
     * Returns the fill colour corresponding to the cell's epidemic state.
     * @param cell the cell to colour
     * @return RGBA fill colour
     */
    protected Color getFillColorFor(Cell cell) {
        switch (cell.getState()) {
            case HEALTHY:   return Color.rgb(89,  180, 90,  0.28);
            case PARTIAL:   return Color.rgb(255, 193, 7,   0.60);
            case INFECTED:  return Color.rgb(220, 53,  69,  0.72);
            case CRITICAL:  return Color.rgb(0,   0,   0,   0.60);
            case RECOVERED: return Color.rgb(89,  90,  210, 0.45);
            default:        return Color.rgb(180, 180, 180, 0.30);
        }
    }

    // -------------------------------------------------------------------------
    // District border rendering
    // -------------------------------------------------------------------------

    /**
     * Draws a 1.5 px black border segment wherever two adjacent Paris cells
     * belong to different arrondissements.
     * @param cellWidth  pixel width of one cell
     * @param cellHeight pixel height of one cell
     */
    private void drawDistrictBorders(double cellWidth, double cellHeight) {
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

    /**
     * Returns true if the neighbour at (nRow, nCol) belongs to a different
     * district than {@code cell}, or if the neighbour is out of bounds / outside Paris.
     */
    private boolean isDifferentDistrict(Cell cell, int nRow, int nCol) {
        if (nRow < 0 || nRow >= parisGrid.getRows() || nCol < 0 || nCol >= parisGrid.getCols()) return true;
        Cell n = parisGrid.getCell(nRow, nCol);
        if (n == null || !n.isInsideParis()) return true;
        String cd = cell.getDistrictId(), nd = n.getDistrictId();
        return cd != null && nd != null && !cd.equals(nd);
    }

    /**
     * Adds a semi-transparent black line segment to the grid layer.
     * The line is mouse-transparent so it does not intercept cell clicks.
     */
    private void addBorderLine(double x1, double y1, double x2, double y2) {
        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(Color.rgb(0, 0, 0, 0.85));
        line.setStrokeWidth(1.5);
        line.setMouseTransparent(true);
        gridLayer.getChildren().add(line);
    }

    // -------------------------------------------------------------------------
    // Statistics
    // -------------------------------------------------------------------------

    /**
     * Records a new statistics snapshot for the current step (if not already done)
     * and notifies sub-classes via {@link #onStatisticsUpdated(Snapshot)}.
     */
    protected void updateStatistics() {
        if (engine.getStepCount() == lastRecordedStep) return;
        Snapshot stats = Statistics.compute(parisGrid, engine.getStepCount());
        statisticHistory.add(stats);
        districtHistory.add(Statistics.computeByDistrict(parisGrid, engine.getStepCount()));
        lastRecordedStep = engine.getStepCount();
        onStatisticsUpdated(stats);
    }

    /** Hook called after each statistics update. Override in sub-classes if needed. */
    protected void onStatisticsUpdated(Snapshot stats) {}

    /**
     * Opens an interactive chart window showing cell-state or population
     * evolution over time, filterable by district and series type.
     */
    @FXML
    public void onShowStatisticsCharts() {
        Stage stage = new Stage();
        stage.setTitle("Graphiques statistiques");

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Step");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nombre de cellules");

        Label percentageLabel = new Label();
        percentageLabel.setStyle("-fx-font-weight: bold;");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Évolution des cellules");

        ComboBox<String> districtComboBox = new ComboBox<>();
        districtComboBox.getItems().add("Global");
        districtComboBox.setValue("Global");
        districtComboBox.setPrefWidth(220);

        if (!districtHistory.isEmpty()) {
            districtHistory.get(districtHistory.size() - 1).keySet().stream()
                .sorted((a, b) -> Integer.compare(Integer.parseInt(a), Integer.parseInt(b)))
                .forEach(districtComboBox.getItems()::add);
        }

        ComboBox<String> statTypeComboBox = new ComboBox<>();
        statTypeComboBox.getItems().addAll("Cellules", "Population");
        statTypeComboBox.setValue("Cellules");
        statTypeComboBox.setPrefWidth(140);

        CheckBox firstCheck  = new CheckBox("Healthy");
        CheckBox secondCheck = new CheckBox("Partial");
        CheckBox thirdCheck  = new CheckBox("Infected");
        CheckBox fourthCheck = new CheckBox("Critical");
        CheckBox fifthCheck  = new CheckBox("Dead");
        CheckBox sixthCheck  = new CheckBox("Recovered");

        fifthCheck.setVisible(false); fifthCheck.setManaged(false);
        firstCheck.setSelected(true); secondCheck.setSelected(true);
        thirdCheck.setSelected(true); fourthCheck.setSelected(true);
        fifthCheck.setSelected(true); sixthCheck.setSelected(true);

        Button updateButton = new Button("Update chart");

        updateButton.setOnAction(event -> {
            chart.getData().clear();
            String dist = districtComboBox.getValue();
            String type = statTypeComboBox.getValue();

            if ("Population".equals(type)) {
                yAxis.setLabel("Population");
                chart.setTitle("Évolution de la population");
                firstCheck.setText("Susceptible"); secondCheck.setText("Exposed");
                thirdCheck.setText("Infected");    fourthCheck.setText("Recovered");
                fifthCheck.setText("Dead");        sixthCheck.setText("Recovered");
                fifthCheck.setVisible(true); fifthCheck.setManaged(true);
                if (firstCheck.isSelected())  chart.getData().add(createSeries(dist, type, "Susceptible", "healthy"));
                if (secondCheck.isSelected()) chart.getData().add(createSeries(dist, type, "Exposed",     "partial"));
                if (thirdCheck.isSelected())  chart.getData().add(createSeries(dist, type, "Infected",    "infected"));
                if (fourthCheck.isSelected()) chart.getData().add(createSeries(dist, type, "Recovered",   "recovered"));
                if (fifthCheck.isSelected())  chart.getData().add(createSeries(dist, type, "Dead",        "dead"));
                if (sixthCheck.isSelected())  chart.getData().add(createSeries(dist, type, "Recovered",   "recovered"));
            } else {
                yAxis.setLabel("Nombre de cellules");
                chart.setTitle("Évolution des cellules");
                firstCheck.setText("Healthy"); secondCheck.setText("Partial");
                thirdCheck.setText("Infected"); fourthCheck.setText("Critical");
                sixthCheck.setText("Recovered");
                fifthCheck.setVisible(false); fifthCheck.setManaged(false);
                if (firstCheck.isSelected())  chart.getData().add(createSeries(dist, type, "Healthy",   "healthy"));
                if (secondCheck.isSelected()) chart.getData().add(createSeries(dist, type, "Partial",   "partial"));
                if (thirdCheck.isSelected())  chart.getData().add(createSeries(dist, type, "Infected",  "infected"));
                if (fourthCheck.isSelected()) chart.getData().add(createSeries(dist, type, "Critical",  "critical"));
                if (sixthCheck.isSelected())  chart.getData().add(createSeries(dist, type, "Recovered", "recovered"));
            }
            updatePercentageLabel(percentageLabel, dist, type);
        });

        districtComboBox.setOnAction(e -> updateButton.fire());
        statTypeComboBox.setOnAction(e -> updateButton.fire());
        firstCheck.setOnAction(e -> updateButton.fire());  secondCheck.setOnAction(e -> updateButton.fire());
        thirdCheck.setOnAction(e -> updateButton.fire());  fourthCheck.setOnAction(e -> updateButton.fire());
        fifthCheck.setOnAction(e -> updateButton.fire());  sixthCheck.setOnAction(e -> updateButton.fire());

        HBox controls = new HBox(12,
            new Label("Zone :"), districtComboBox,
            new Label("Stats :"), statTypeComboBox,
            firstCheck, secondCheck, thirdCheck, fourthCheck, fifthCheck, sixthCheck,
            updateButton);
        controls.setStyle("-fx-padding: 10; -fx-alignment: center-left;");

        VBox root = new VBox(10, percentageLabel, controls, chart);
        updateButton.fire();
        stage.setScene(new Scene(root, 1000, 600));
        stage.show();
    }

    /**
     * Builds one named data series for the statistics chart.
     * @param dist     district ID or "Global"
     * @param statType "Cellules" or "Population"
     * @param name     series display name
     * @param type     internal key ("healthy", "infected", etc.)
     * @return populated chart series
     */
    private XYChart.Series<Number, Number> createSeries(String dist, String statType, String name, String type) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        if ("Global".equals(dist)) {
            for (Snapshot s : statisticHistory)
                series.getData().add(new XYChart.Data<>(s.getStep(), getGlobalValue(s, statType, type)));
        } else {
            for (Map<String, DistrictSnapshot> stepMap : districtHistory) {
                DistrictSnapshot s = stepMap.get(dist);
                if (s != null)
                    series.getData().add(new XYChart.Data<>(s.getStep(), getDistrictValue(s, statType, type)));
            }
        }
        return series;
    }

    /**
     * Extracts the value for the given statistic type from a global snapshot.
     * @param s        the snapshot to read
     * @param statType "Cellules" or "Population"
     * @param type     compartment key
     * @return the numeric value, or 0 if unknown
     */
    private double getGlobalValue(Snapshot s, String statType, String type) {
        if ("Cellules".equals(statType)) switch (type) {
            case "healthy":   return s.getHealthyCells();
            case "partial":   return s.getPartialCells();
            case "infected":  return s.getInfectedCells();
            case "critical":  return s.getCriticalCells();
            case "recovered": return s.getRecoveredPopulation();
            case "dead":      return s.getDeadPopulation();
            default:          return 0;
        }
        switch (type) {
            case "healthy":   return s.getSusceptiblePopulation();
            case "partial":   return s.getExposedPopulation();
            case "infected":  return s.getInfectedPopulation();
            case "critical":  return s.getDeadPopulation();
            case "recovered": return s.getRecoveredPopulation();
            default:          return 0;
        }
    }

    /**
     * Extracts the value for the given statistic type from a district snapshot.
     * @param s        the district snapshot to read
     * @param statType "Cellules" or "Population"
     * @param type     compartment key
     * @return the numeric value, or 0 if unknown
     */
    private double getDistrictValue(DistrictSnapshot s, String statType, String type) {
        if ("Cellules".equals(statType)) switch (type) {
            case "healthy":   return s.getHealthyCells();
            case "partial":   return s.getPartialCells();
            case "infected":  return s.getInfectedCells();
            case "critical":  return s.getCriticalCells();
            case "recovered": return s.getRecoveredCells();
            default:          return 0;
        }
        switch (type) {
            case "healthy":   return s.getSusceptiblePopulation();
            case "partial":   return s.getExposedPopulation();
            case "infected":  return s.getInfectedPopulation();
            case "critical":  return s.getDeadPopulation();
            default:          return 0;
        }
    }

    /**
     * Refreshes the summary percentage label above the chart
     * for the currently selected district and statistic type.
     * @param lbl      the label to update
     * @param dist     district ID or "Global"
     * @param statType "Cellules" or "Population"
     */
    private void updatePercentageLabel(Label lbl, String dist, String statType) {
        if ("Global".equals(dist)) {
            if (statisticHistory.isEmpty()) return;
            Snapshot last = statisticHistory.get(statisticHistory.size() - 1);
            if ("Cellules".equals(statType)) {
                lbl.setText(String.format(
                    "Global - Cellules | Healthy: %.2f%% | Partial: %.2f%% | Infected: %.2f%% | Critical: %.2f%% | Recovered: %.2f%%",
                    last.getHealthyCellPercentage(), last.getPartialCellPercentage(),
                    last.getInfectedCellPercentage(), last.getCriticalCellPercentage(),
                    last.getRecoveredCellPercentage()));
            } else {
                lbl.setText(String.format(
                    "Global - Population | Susceptible: %.2f%% | Exposed: %.2f%% | Infected: %.2f%% | Recovered: %.2f%% | Dead: %.2f%%",
                    percent(last.getSusceptiblePopulation(), last.getTotalPopulation()),
                    percent(last.getExposedPopulation(),     last.getTotalPopulation()),
                    percent(last.getInfectedPopulation(),    last.getTotalPopulation()),
                    percent(last.getRecoveredPopulation(),   last.getTotalPopulation()),
                    percent(last.getDeadPopulation(),        last.getTotalPopulation())));
            }
        } else {
            if (districtHistory.isEmpty()) return;
            DistrictSnapshot last = districtHistory.get(districtHistory.size() - 1).get(dist);
            if (last == null) return;
            if ("Cellules".equals(statType)) {
                lbl.setText(String.format(
                    "District %s - Cellules | Healthy: %.2f%% | Partial: %.2f%% | Infected: %.2f%% | Critical: %.2f%% | Recovered: %.2f%%",
                    dist,
                    percent(last.getHealthyCells(),  last.getTotalCells()),
                    percent(last.getPartialCells(),  last.getTotalCells()),
                    percent(last.getInfectedCells(), last.getTotalCells()),
                    percent(last.getCriticalCells(), last.getTotalCells()),
                    percent(last.getRecoveredCells(), last.getTotalCells())));
            } else {
                lbl.setText(String.format(
                    "District %s - Population | Susceptible: %.2f%% | Exposed: %.2f%% | Infected: %.2f%% | Recovered: %.2f%% | Dead: %.2f%%",
                    dist,
                    percent(last.getSusceptiblePopulation(), last.getTotalPopulation()),
                    percent(last.getExposedPopulation(),     last.getTotalPopulation()),
                    percent(last.getInfectedPopulation(),    last.getTotalPopulation()),
                    percent(last.getRecoveredPopulation(),   last.getTotalPopulation()),
                    percent(last.getDeadPopulation(),        last.getTotalPopulation())));
            }
        }
    }

    /**
     * Computes the percentage of {@code v} relative to {@code total}.
     * Returns 0 if {@code total} is zero to avoid division by zero.
     * @param v     partial value
     * @param total total value
     * @return percentage in [0, 100]
     */
    protected double percent(double v, double total) {
        return total == 0 ? 0 : v * 100.0 / total;
    }
}
