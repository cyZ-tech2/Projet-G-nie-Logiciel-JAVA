package com.groupg.cells2d.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.groupg.cells2d.engine.Propagation;
import com.groupg.cells2d.engine.SimulationEngine;
import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.Grid;
import com.groupg.cells2d.model.board.SEIRData;
import com.groupg.cells2d.model.board.SimulationParams;
import com.groupg.cells2d.model.board.ParisGridFactory;
import com.groupg.cells2d.model.enums.CellState;
import com.groupg.cells2d.model.enums.SimStatus;
import com.groupg.cells2d.stats.DistrictSnapshot;
import com.groupg.cells2d.stats.Snapshot;
import com.groupg.cells2d.stats.Statistics;
import javafx.animation.FillTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.groupg.cells2d.data.SaveManager;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.scene.web.WebView;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.PrintWriter;
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
    @FXML private Slider speedSlider;
    @FXML private Label mortalityLabel;
    @FXML private Label propagationLabel;
    @FXML private Label betaLabel;
    @FXML private Label gammaLabel;
    @FXML private Label sigmaLabel;
    @FXML private Label xiLabel;
    @FXML private Label statsLabel;
    @FXML private Label speedLabel;



    // --- State ---
    private Grid parisGrid;
    private SimulationEngine engine;
    private Propagation propagation;
    private double canvasWidth;
    private double canvasHeight;
    private int speed;
    
    //---Satistics--
    private final List<Snapshot> statisticHistory = new ArrayList<>();
    private int lastRecordedStep = -1;
    private final List<Map<String, DistrictSnapshot>> districtHistory = new ArrayList<>();

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

        speedSlider.setValue(engine.getStepDuration());
        speedSlider.valueProperty().addListener((obs,o,n)->{
            engine.setStepDuration(n.intValue());
            speedLabel.setText(String.format("Vitesse: %d",n.intValue()));
        });
        speedLabel.setText(String.format("Vitesse: %d",engine.getStepDuration()));
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
        statisticHistory.clear();

        updateStatistics();
        //seedInitialInfection();
        
        updateStatusUI();
        drawMap();
    }

    @FXML public void onStep() {
        if (engine.getStatus() != SimStatus.RUNNING) {
            engine.step();
            updateStatistics();
            updateStatusUI();
            drawGrid();
        }
    }


    private void startUIRefreshLoop() {
        Thread t = new Thread(() -> {
            while (engine.getStatus() == SimStatus.RUNNING) {
                Platform.runLater(() -> { drawGrid(); updateStatusUI(); });
                Platform.runLater(() -> {                  
                    updateStatistics();
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
                updateStatistics(); 
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
        if (state == CellState.CRITICAL) return CellState.RECOVERED;
        return CellState.HEALTHY;
    }

    private Color getColorFor(Cell cell) {
        switch (cell.getState()) {
            case HEALTHY:  return Color.rgb(89,  180, 90,  0.28);
            case PARTIAL:  return Color.rgb(255, 193, 7,   0.60);
            case INFECTED: return Color.rgb(220, 53,  69,  0.72);
            case CRITICAL: return Color.rgb(0,   0,   0,   0.60);
            case RECOVERED: return Color.rgb(89, 90, 210, 0.45);
            default:       return Color.rgb(180, 180, 180, 0.30);
        }
    }

    private void updateStatisticsUI(Snapshot stats) {
        statsLabel.setText(
                "Cellules :"+
                "\nHealthy : " + stats.getHealthyCells()
                + "\nPartial : " + stats.getPartialCells()
                + "\nInfected : " + stats.getInfectedCells()
                + "\nCritical : " + stats.getCriticalCells()
                + "\nRecovered :" + stats.getRecoveredCells()
                + "\nTotal : " + stats.getTotalCells()
                + "\n\nPopulation"
                + "\nTotal pop : " + String.format("%.0f", stats.getTotalPopulation())
                + "\nSusceptible : " + String.format("%.0f", stats.getSusceptiblePopulation())
                + "\nExposed : " + String.format("%.0f", stats.getExposedPopulation())
                + "\nInfected pop : " + String.format("%.0f", stats.getInfectedPopulation())
                + "\nRecovered : " + String.format("%.0f", stats.getRecoveredPopulation())
                + "\nDead : " + String.format("%.0f", stats.getDeadPopulation())
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

        Label percentageLabel = new Label();
        percentageLabel.setStyle("-fx-font-weight: bold;");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Évolution des cellules");

        ComboBox<String> districtComboBox = new ComboBox<>();
        districtComboBox.getItems().add("Global");
        districtComboBox.setValue("Global");
        districtComboBox.setPrefWidth(220);

        if (!districtHistory.isEmpty()) {
            Map<String, DistrictSnapshot> lastStep =
                    districtHistory.get(districtHistory.size() - 1);

            lastStep.keySet().stream()
                    .sorted((a, b) -> Integer.compare(
                            Integer.parseInt(a),
                            Integer.parseInt(b)
                    ))
                    .forEach(districtComboBox.getItems()::add);
        }

        ComboBox<String> statTypeComboBox = new ComboBox<>();
        statTypeComboBox.getItems().addAll("Cellules", "Population");
        statTypeComboBox.setValue("Cellules");
        statTypeComboBox.setPrefWidth(140);

        CheckBox firstCheck = new CheckBox("Healthy");
        CheckBox secondCheck = new CheckBox("Partial");
        CheckBox thirdCheck = new CheckBox("Infected");
        CheckBox fourthCheck = new CheckBox("Critical");
        CheckBox fifthCheck = new CheckBox("Dead");
        CheckBox sixthCheck = new CheckBox("Recovered");

        fifthCheck.setVisible(false);
        fifthCheck.setManaged(false);

        firstCheck.setSelected(true);
        secondCheck.setSelected(true);
        thirdCheck.setSelected(true);
        fourthCheck.setSelected(true);
        fifthCheck.setSelected(true);
        sixthCheck.setSelected(true);

        Button updateButton = new Button("Update chart");

        updateButton.setOnAction(event -> {
            chart.getData().clear();

            String selectedDistrict = districtComboBox.getValue();
            String selectedStatType = statTypeComboBox.getValue();

            if ("Population".equals(selectedStatType)) {
                yAxis.setLabel("Population");
                chart.setTitle("Évolution de la population");

                firstCheck.setText("Susceptible");
                secondCheck.setText("Exposed");
                thirdCheck.setText("Infected");
                fourthCheck.setText("Recovered");
                fifthCheck.setText("Dead");
                sixthCheck.setText("Recovered");

                fifthCheck.setVisible(true);
                fifthCheck.setManaged(true);

                if (firstCheck.isSelected()) {
                    chart.getData().add(createSeries(selectedDistrict, selectedStatType, "Susceptible", "healthy"));
                }
                if (secondCheck.isSelected()) {
                    chart.getData().add(createSeries(selectedDistrict, selectedStatType, "Exposed", "partial"));
                }
                if (thirdCheck.isSelected()) {
                    chart.getData().add(createSeries(selectedDistrict, selectedStatType, "Infected", "infected"));
                }
                if (fourthCheck.isSelected()) {
                    chart.getData().add(createSeries(selectedDistrict, selectedStatType, "Recovered", "recovered"));
                }
                if (fifthCheck.isSelected()) {
                    chart.getData().add(createSeries(selectedDistrict, selectedStatType, "Dead", "dead"));
                }
                if (sixthCheck.isSelected()) {
                    chart.getData().add(createSeries(selectedDistrict, selectedStatType, "Recovered", "recovered"));
                }
            } else {
                yAxis.setLabel("Nombre de cellules");
                chart.setTitle("Évolution des cellules");

                firstCheck.setText("Healthy");
                secondCheck.setText("Partial");
                thirdCheck.setText("Infected");
                fourthCheck.setText("Critical");
                sixthCheck.setText("Recovered");

                fifthCheck.setVisible(false);
                fifthCheck.setManaged(false);

                if (firstCheck.isSelected()) {
                    chart.getData().add(createSeries(selectedDistrict, selectedStatType, "Healthy", "healthy"));
                }
                if (secondCheck.isSelected()) {
                    chart.getData().add(createSeries(selectedDistrict, selectedStatType, "Partial", "partial"));
                }
                if (thirdCheck.isSelected()) {
                    chart.getData().add(createSeries(selectedDistrict, selectedStatType, "Infected", "infected"));
                }
                if (fourthCheck.isSelected()) {
                    chart.getData().add(createSeries(selectedDistrict, selectedStatType, "Critical", "critical"));
                }
                if (sixthCheck.isSelected()) {
                    chart.getData().add(createSeries(selectedDistrict, selectedStatType, "Recovered", "recovered"));
                }
            }

            updatePercentageLabel(percentageLabel, selectedDistrict, selectedStatType);
        });

        districtComboBox.setOnAction(event -> updateButton.fire());
        statTypeComboBox.setOnAction(event -> updateButton.fire());
        firstCheck.setOnAction(event -> updateButton.fire());
        secondCheck.setOnAction(event -> updateButton.fire());
        thirdCheck.setOnAction(event -> updateButton.fire());
        fourthCheck.setOnAction(event -> updateButton.fire());
        fifthCheck.setOnAction(event -> updateButton.fire());
        sixthCheck.setOnAction(event -> updateButton.fire());

        HBox checkBoxBox = new HBox(
                12,
                new Label("Zone :"),
                districtComboBox,
                new Label("Stats :"),
                statTypeComboBox,
                firstCheck,
                secondCheck,
                thirdCheck,
                fourthCheck,
                fifthCheck,
                sixthCheck,
                updateButton
        );
        checkBoxBox.setStyle("-fx-padding: 10; -fx-alignment: center-left;");

        VBox root = new VBox(10, percentageLabel, checkBoxBox, chart);

        updateButton.fire();

        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.show();
    }

    private XYChart.Series<Number, Number> createSeries(
        String selectedDistrict,
        String selectedStatType,
        String name,
        String type ) {

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);

        if ("Global".equals(selectedDistrict)) {
            for (Snapshot snapshot : statisticHistory) {
                series.getData().add(
                        new XYChart.Data<>(
                                snapshot.getStep(),
                                getGlobalValue(snapshot, selectedStatType, type)
                        )
                );
            }
        } else {
            for (Map<String, DistrictSnapshot> stepMap : districtHistory) {
                DistrictSnapshot snapshot = stepMap.get(selectedDistrict);

                if (snapshot == null) {
                    continue;
                }

                series.getData().add(
                        new XYChart.Data<>(
                                snapshot.getStep(),
                                getDistrictValue(snapshot, selectedStatType, type)
                        )
                );
            }
        }

        return series;
    }

    private void updateStatistics() {
         if (engine.getStepCount() == lastRecordedStep) {
            return;
        }

        Snapshot stats = Statistics.compute(parisGrid, engine.getStepCount());

        statisticHistory.add(stats);
        districtHistory.add(Statistics.computeByDistrict(parisGrid, engine.getStepCount()));
        updateStatisticsUI(stats);

        lastRecordedStep = engine.getStepCount();
    }

   private double getGlobalValue(
        Snapshot snapshot,
        String selectedStatType,
        String type) {

        if ("Cellules".equals(selectedStatType)) {
            switch (type) {
                case "healthy":
                    return snapshot.getHealthyCells();
                case "partial":
                    return snapshot.getPartialCells();
                case "infected":
                    return snapshot.getInfectedCells();
                case "critical":
                    return snapshot.getCriticalCells();
                    case "recovered":
                    return snapshot.getRecoveredPopulation();
                case "dead":
                    return snapshot.getDeadPopulation();
                default:
                    return 0;
            }
        }

        // Population
        switch (type) {
            case "healthy":
                return snapshot.getSusceptiblePopulation();
            case "partial":
                return snapshot.getExposedPopulation();
            case "infected":
                return snapshot.getInfectedPopulation();
            case "critical":
                return snapshot.getDeadPopulation();
            case "recovered":
                return snapshot.getRecoveredPopulation();
            default:
                return 0;
        }
    }

   private double getDistrictValue(
        DistrictSnapshot snapshot,
        String selectedStatType,
        String type) {

        if ("Cellules".equals(selectedStatType)) {
            switch (type) {
                case "healthy":
                    return snapshot.getHealthyCells();
                case "partial":
                    return snapshot.getPartialCells();
                case "infected":
                    return snapshot.getInfectedCells();
                case "critical":
                    return snapshot.getCriticalCells();
                case "recovered":
                    return snapshot.getRecoveredCells();
                default:
                    return 0;
            }
        }

        // Population
        switch (type) {
            case "healthy":
                return snapshot.getSusceptiblePopulation();
            case "partial":
                return snapshot.getExposedPopulation();
            case "infected":
                return snapshot.getInfectedPopulation();
            case "critical":
                return snapshot.getDeadPopulation();
            default:
                return 0;
        }
    }

    private void updatePercentageLabel(
        Label percentageLabel,
        String selectedDistrict,
        String selectedStatType) {

        if ("Global".equals(selectedDistrict)) {
            if (statisticHistory.isEmpty()) return;

            Snapshot last = statisticHistory.get(statisticHistory.size() - 1);

            if ("Cellules".equals(selectedStatType)) {
                percentageLabel.setText(
                        String.format(
                                "Global - Cellules | Healthy: %.2f%% | Partial: %.2f%% | Infected: %.2f%% | Critical: %.2f%% | Recovered: %.2f%% ",
                                last.getHealthyCellPercentage(),
                                last.getPartialCellPercentage(),
                                last.getInfectedCellPercentage(),
                                last.getCriticalCellPercentage(),
                                last.getRecoveredCellPercentage()
                        )
                );
            } else {
                percentageLabel.setText(
                        String.format(
                                "Global - Population | Susceptible: %.2f%% | Exposed: %.2f%% | Infected: %.2f%% | Recovered: %.2f%% | Dead: %.2f%%",
                                percent(last.getSusceptiblePopulation(), last.getTotalPopulation()),
                                percent(last.getExposedPopulation(), last.getTotalPopulation()),
                                percent(last.getInfectedPopulation(), last.getTotalPopulation()),
                                percent(last.getRecoveredPopulation(), last.getTotalPopulation()),
                                percent(last.getDeadPopulation(), last.getTotalPopulation())
                        )
                );
            }
        } else {
            if (districtHistory.isEmpty()) return;

            Map<String, DistrictSnapshot> lastStep =
                    districtHistory.get(districtHistory.size() - 1);

            DistrictSnapshot last = lastStep.get(selectedDistrict);
            if (last == null) return;

            if ("Cellules".equals(selectedStatType)) {
                percentageLabel.setText(
                        String.format(
                                "Global - Cellules | Healthy: %.2f%% | Partial: %.2f%% | Infected: %.2f%% | Critical: %.2f%% | Recovered: %.2f%% ",
                                selectedDistrict,
                                percent(last.getHealthyCells(), last.getTotalCells()),
                                percent(last.getPartialCells(), last.getTotalCells()),
                                percent(last.getInfectedCells(), last.getTotalCells()),
                                percent(last.getCriticalCells(), last.getTotalCells()),
                                percent(last.getRecoveredCells(),last.getTotalCells())
                        )
                );
            } else {
                percentageLabel.setText(
                        String.format(
                                "District %s - Population | Susceptible: %.2f%% | Exposed: %.2f%% | Infected: %.2f%% | Recovered: %.2f%% | Dead: %.2f%%",
                                selectedDistrict,
                                percent(last.getSusceptiblePopulation(), last.getTotalPopulation()),
                                percent(last.getExposedPopulation(), last.getTotalPopulation()),
                                percent(last.getInfectedPopulation(), last.getTotalPopulation()),
                                percent(last.getRecoveredPopulation(), last.getTotalPopulation()),
                                percent(last.getDeadPopulation(), last.getTotalPopulation())
                        )
                );
            }
        }
    }

    private double percent(double value, double total) {
        return total == 0 ? 0 : value * 100.0 / total;
    }


    /**
     *  Saves the current simulation state into a binary file.
     * A file chooser is displayed to let the user select
     * the destination file
     */
    @FXML
    public void onSaveSimulation() {
        try {
            //Pause simulation before saving 
            engine.pause();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save simulation");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Simulation files", "*.dat")
            );

            //Open save dialog
            File file = fileChooser.showSaveDialog(mapPane.getScene().getWindow());

            if (file != null) {
                String path = file.getAbsolutePath();

                // Automatically add .dat extension if missing
                if (!path.toLowerCase().endsWith(".dat")) {
                    path += ".dat";
                }
                //save simulation into the selected file
                SaveManager.save(engine, path);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Save");
                alert.setHeaderText(null);
                alert.setContentText("Simulation saved successfully");
                alert.showAndWait();            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Save error");
        }
    }

    /**
     * Loads a previously saved simulation from a binary file 
     * Simulation is restired 
     * Interface is refreshed
     */
    @FXML
    public void onLoadSimulation() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load simulation");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Simulation files", "*.dat")
            );

            //Open the file selection dialog
            File file = fileChooser.showOpenDialog(mapPane.getScene().getWindow());

            if (file != null) {
                //Restore simulation from file 
                engine = SaveManager.load(file.getAbsolutePath());
                parisGrid = engine.getGrid();
                // Refresh interface after loading 
                updateStatistics();
                updateStatusUI();
                drawMap();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Load");
                alert.setHeaderText(null);
                alert.setContentText("Simulation loaded successfully");
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Load error");
        }
    }


    /**
     * Resets the current simulation and creates a new empty simulation.
     */
    @FXML
    public void onNewSimulation() {
        onStop();
    }

    /**
     * Closes the current application window.
     */
    @FXML
    public void onCloseApplication() {
        try {
            engine.pause();
            mapPane.getScene().getWindow().hide();

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Close error");
        }
    }

    /**
     * Displays the About page in a local web view.
     */
    @FXML
    public void onAbout() {
        WebView webView = new WebView();

        String aboutPage = getClass()
                .getResource("/com.groupg.cells2d.controller/about.html")
                .toExternalForm();

        webView.getEngine().load(aboutPage);

        Stage stage = new Stage();
        stage.setTitle("About Cells2D");
        stage.setScene(new Scene(webView, 700, 500));
        stage.show();
    }
    // ----- Exportation des stats -----//
    /**
     * Exports all main simulation data:
     * statistics as CSV, statistics as TXT, and the current map as PNG.
     */
    @FXML
    public void onExportAll() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose export folder");

            File folder = directoryChooser.showDialog(mapPane.getScene().getWindow());

            if (folder != null) {

                File csvFile = new File(folder, "statistics.csv");
                try (PrintWriter writer = new PrintWriter(csvFile)) {
                    writer.println("Statistic,Value");
                    writer.println("Step," + engine.getStepCount());
                    writer.println("Statistics,\"" + statsLabel.getText().replace("\n", " | ") + "\"");
                }

                File txtFile = new File(folder, "statistics.txt");
                try (PrintWriter writer = new PrintWriter(txtFile)) {
                    writer.println("Cells2D Simulation Statistics");
                    writer.println("-----------------------------");
                    writer.println("Step: " + engine.getStepCount());
                    writer.println();
                    writer.println(statsLabel.getText());
                }

                File pngFile = new File(folder, "map.png");
                WritableImage image = mapPane.snapshot(null, null);
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", pngFile);

                statusLabel.setText("Export completed successfully");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Export error");
        }
    }
}
