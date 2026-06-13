package com.groupg.cells2d.controller;

import com.groupg.cells2d.engine.Propagation;
import com.groupg.cells2d.engine.SimulationEngine;
import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.ParisGridFactory;
import com.groupg.cells2d.model.board.SEIRData;
import com.groupg.cells2d.model.board.SimulationParams;
import com.groupg.cells2d.model.enums.CellState;
import com.groupg.cells2d.model.enums.SimStatus;
import com.groupg.cells2d.stats.Snapshot;
import com.groupg.cells2d.stats.Statistics;
import com.groupg.cells2d.data.SaveManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.web.WebView;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.PrintWriter;

public class ResearcherController extends AbstractSimController {

    // --- Modes ---
    @FXML private RadioButton cursorRadio;
    @FXML private RadioButton brushRadio;
    @FXML private RadioButton bucketRadio;

    // --- Controls ---
    @FXML private Button btnPlay;
    @FXML private Button btnPause;
    @FXML private Button btnStop;
    @FXML private Button btnStep;
    @FXML private Button btnRewind;

    // --- Status ---
    @FXML private Label selectedCellLabel;
    @FXML private Label statsLabel;

    // --- Sliders ---
    @FXML private Slider betaSlider;
    @FXML private Slider gammaSlider;
    @FXML private Slider sigmaSlider;
    @FXML private Slider xiSlider;
    @FXML private Slider mortalitySlider;
    @FXML private Slider propagationSlider;
    @FXML private Slider speedSlider;
    @FXML private Label betaLabel;
    @FXML private Label gammaLabel;
    @FXML private Label sigmaLabel;
    @FXML private Label xiLabel;
    @FXML private Label mortalityLabel;
    @FXML private Label propagationLabel;
    @FXML private Label speedLabel;

    private Propagation propagation;

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    @FXML
    public void initialize() {
        parisGrid   = ParisGridFactory.createDefaultParisGrid();
        propagation = new Propagation();
        engine      = new SimulationEngine(parisGrid, propagation);

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
            xiLabel.setText(String.format("ξ (déclin immunité): %.2f", n.doubleValue()));
        });
        xiLabel.setText(String.format("ξ (déclin immunité): %.2f", propagation.getParams().getXi()));

        mortalitySlider.setValue(propagation.getParams().getMortalityRate());
        mortalitySlider.valueProperty().addListener((obs, o, n) -> {
            propagation.getParams().setMortalityRate(n.doubleValue());
            mortalityLabel.setText(String.format("Taux de mortalité: %.2f", n.doubleValue()));
        });
        mortalityLabel.setText(String.format("Taux de mortalité: %.2f", propagation.getParams().getMortalityRate()));

        propagationSlider.setValue(propagation.getParams().getPropagationRate());
        propagationSlider.valueProperty().addListener((obs, o, n) -> {
            propagation.getParams().setPropagationRate(n.doubleValue());
            propagationLabel.setText(String.format("Taux de propagation: %.2f", n.doubleValue()));
        });
        propagationLabel.setText(String.format("Taux de propagation: %.2f", propagation.getParams().getPropagationRate()));

        speedSlider.setValue(engine.getStepDuration());
        speedSlider.valueProperty().addListener((obs, o, n) -> {
            engine.setStepDuration(n.intValue());
            speedLabel.setText(String.format("Vitesse: %d", n.intValue()));
        });
        speedLabel.setText(String.format("Vitesse: %d", engine.getStepDuration()));
    }

    // -------------------------------------------------------------------------
    // Contrôles spécifiques chercheur
    // -------------------------------------------------------------------------

    @FXML public void onStop() {
        engine.stop();
        parisGrid   = ParisGridFactory.createDefaultParisGrid();
        propagation = new Propagation(new SimulationParams(
            propagation.getParams().getBeta(),
            propagation.getParams().getSigma(),
            propagation.getParams().getGamma(),
            propagation.getParams().getPropagationRate(),
            propagation.getParams().getMortalityRate()
        ));
        engine = new SimulationEngine(parisGrid, propagation);
        statisticHistory.clear();
        districtHistory.clear();
        lastRecordedStep = -1;
        updateStatistics();
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

    @FXML public void onRewind() {
        if (engine.getStatus() != SimStatus.RUNNING) {
            if (engine.rewind()) {
                parisGrid = engine.getGrid();
                lastRecordedStep = -1;
                updateStatistics();
                updateStatusUI();
                drawGrid();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Hook : clic sur cellule (cursor / brush / bucket)
    // -------------------------------------------------------------------------

    @Override
    protected void onCellClicked(Cell cell) {
        if (cursorRadio.isSelected()) {
            selectCell(cell);
        } else if (bucketRadio.isSelected()) {
            bucketFill(cell);
        }
        // brush est géré via onMousePressed dans drawGrid override
    }

    @Override
    protected void drawGrid() {
        gridLayer.getChildren().clear();

        double cellWidth  = canvasWidth  / parisGrid.getCols();
        double cellHeight = canvasHeight / parisGrid.getRows();

        for (int row = 0; row < parisGrid.getRows(); row++) {
            for (int col = 0; col < parisGrid.getCols(); col++) {
                Cell cell = parisGrid.getCell(row, col);
                if (cell == null || !cell.isInsideParis()) continue;

                javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(
                    col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                rect.setFill(getFillColorFor(cell));
                rect.setStroke(getCellStrokeColor(cell));
                rect.setStrokeWidth(getCellStrokeWidth(cell));

                rect.setOnMouseClicked(event -> onCellClicked(cell));
                rect.setOnMousePressed(event -> {
                    if (brushRadio.isSelected()) paintCell(cell, rect);
                });

                gridLayer.getChildren().add(rect);
            }
        }

        // Bordures districts via méthode parente (réflexion non accessible → on redessine ici)
        drawDistrictBordersInternal(cellWidth, cellHeight);
    }

    private void drawDistrictBordersInternal(double cellWidth, double cellHeight) {
        for (int row = 0; row < parisGrid.getRows(); row++) {
            for (int col = 0; col < parisGrid.getCols(); col++) {
                Cell cell = parisGrid.getCell(row, col);
                if (cell == null || !cell.isInsideParis()) continue;
                if (isDifferentDistrictR(cell, row, col+1)) addBorderLineR((col+1)*cellWidth, row*cellHeight,    (col+1)*cellWidth, (row+1)*cellHeight);
                if (isDifferentDistrictR(cell, row+1, col)) addBorderLineR(col*cellWidth,     (row+1)*cellHeight, (col+1)*cellWidth, (row+1)*cellHeight);
                if (isDifferentDistrictR(cell, row, col-1)) addBorderLineR(col*cellWidth,     row*cellHeight,     col*cellWidth,     (row+1)*cellHeight);
                if (isDifferentDistrictR(cell, row-1, col)) addBorderLineR(col*cellWidth,     row*cellHeight,     (col+1)*cellWidth, row*cellHeight);
            }
        }
    }

    private boolean isDifferentDistrictR(Cell cell, int nRow, int nCol) {
        if (nRow < 0 || nRow >= parisGrid.getRows() || nCol < 0 || nCol >= parisGrid.getCols()) return true;
        Cell n = parisGrid.getCell(nRow, nCol);
        if (n == null || !n.isInsideParis()) return true;
        String cd = cell.getDistrictId(), nd = n.getDistrictId();
        return cd != null && nd != null && !cd.equals(nd);
    }

    private void addBorderLineR(double x1, double y1, double x2, double y2) {
        javafx.scene.shape.Line line = new javafx.scene.shape.Line(x1, y1, x2, y2);
        line.setStroke(Color.rgb(0, 0, 0, 0.85));
        line.setStrokeWidth(1.5);
        line.setMouseTransparent(true);
        gridLayer.getChildren().add(line);
    }

    @Override
    protected Color getCellStrokeColor(Cell cell) {
        if (!cell.isAlive()) return Color.rgb(255, 0, 0, 0.85);
        return Color.rgb(20, 20, 20, 0.22);
    }

    @Override
    protected double getCellStrokeWidth(Cell cell) {
        return cell.isAlive() ? 0.35 : 3.0;
    }

    // -------------------------------------------------------------------------
    // Modes dessin
    // -------------------------------------------------------------------------

    private void selectCell(Cell cell) {
        SEIRData d = cell.getSeirData();
        selectedCellLabel.setText(
            cell.getCellId() + " | " + cell.getDistrictName()
            + " | état: " + cell.getState()
            + " | pop: " + cell.getPopulation()
            + String.format(" | S:%.0f E:%.0f I:%.0f R:%.0f D:%.0f",
                d.getSusceptible(), d.getExposed(), d.getInfected(), d.getRecovered(), d.getDead()));
    }

    private void paintCell(Cell cell, javafx.scene.shape.Rectangle rect) {
        cell.setState(nextState(cell.getState()));
        syncSeirToState(cell);
        rect.setFill(getFillColorFor(cell));
    }

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
        drawGrid();
    }

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

    private CellState nextState(CellState state) {
        if (state == CellState.HEALTHY)   return CellState.PARTIAL;
        if (state == CellState.PARTIAL)   return CellState.INFECTED;
        if (state == CellState.INFECTED)  return CellState.CRITICAL;
        if (state == CellState.CRITICAL)  return CellState.RECOVERED;
        return CellState.HEALTHY;
    }

    private void updateStatisticsUI(Snapshot stats) {
        statsLabel.setText(
            "Cellules :"
            + "\nHealthy : "   + stats.getHealthyCells()
            + "\nPartial : "   + stats.getPartialCells()
            + "\nInfected : "  + stats.getInfectedCells()
            + "\nCritical : "  + stats.getCriticalCells()
            + "\nRecovered :"  + stats.getRecoveredCells()
            + "\nTotal : "     + stats.getTotalCells()
            + "\n\nPopulation"
            + "\nTotal pop : "     + String.format("%.0f", stats.getTotalPopulation())
            + "\nSusceptible : "   + String.format("%.0f", stats.getSusceptiblePopulation())
            + "\nExposed : "       + String.format("%.0f", stats.getExposedPopulation())
            + "\nInfected pop : "  + String.format("%.0f", stats.getInfectedPopulation())
            + "\nRecovered : "     + String.format("%.0f", stats.getRecoveredPopulation())
            + "\nDead : "          + String.format("%.0f", stats.getDeadPopulation()));
    }

    // -------------------------------------------------------------------------
    // Sauvegarde / chargement / export
    // -------------------------------------------------------------------------

    @FXML public void onSaveSimulation() {
        try {
            engine.pause();
            FileChooser fc = new FileChooser();
            fc.setTitle("Save simulation");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Simulation files", "*.dat"));
            File file = fc.showSaveDialog(mapPane.getScene().getWindow());
            if (file != null) {
                String path = file.getAbsolutePath();
                if (!path.toLowerCase().endsWith(".dat")) path += ".dat";
                SaveManager.save(engine, path);
                new Alert(Alert.AlertType.INFORMATION, "Simulation saved successfully", ButtonType.OK).showAndWait();
            }
        } catch (Exception e) { e.printStackTrace(); statusLabel.setText("Save error"); }
    }

    @FXML public void onLoadSimulation() {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Load simulation");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Simulation files", "*.dat"));
            File file = fc.showOpenDialog(mapPane.getScene().getWindow());
            if (file != null) {
                engine    = SaveManager.load(file.getAbsolutePath());
                parisGrid = engine.getGrid();
                updateStatistics();
                updateStatusUI();
                drawMap();
                new Alert(Alert.AlertType.INFORMATION, "Simulation loaded successfully", ButtonType.OK).showAndWait();
            }
        } catch (Exception e) { e.printStackTrace(); statusLabel.setText("Load error"); }
    }

    @FXML public void onNewSimulation() { onStop(); }



    @FXML public void onAbout() {
        WebView webView = new WebView();
        webView.getEngine().load(
            getClass().getResource("/com.groupg.cells2d.controller/about.html").toExternalForm());
        Stage stage = new Stage();
        stage.setTitle("About Cells2D");
        stage.setScene(new Scene(webView, 700, 500));
        stage.show();
    }

    @FXML public void onExportAll() {
        try {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("Choose export folder");
            File folder = dc.showDialog(mapPane.getScene().getWindow());
            if (folder != null) {
                try (PrintWriter w = new PrintWriter(new File(folder, "statistics.csv"))) {
                    w.println("Statistic,Value");
                    w.println("Step," + engine.getStepCount());
                    w.println("Statistics,\"" + statsLabel.getText().replace("\n", " | ") + "\"");
                }
                try (PrintWriter w = new PrintWriter(new File(folder, "statistics.txt"))) {
                    w.println("Cells2D Simulation Statistics");
                    w.println("-----------------------------");
                    w.println("Step: " + engine.getStepCount());
                    w.println();
                    w.println(statsLabel.getText());
                }
                WritableImage image = mapPane.snapshot(null, null);
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File(folder, "map.png"));
                statusLabel.setText("Export completed successfully");
            }
        } catch (Exception e) { e.printStackTrace(); statusLabel.setText("Export error"); }
    }
}
