package com.groupg.cells2d.controller;

import com.groupg.cells2d.data.SaveManager;
import com.groupg.cells2d.engine.Propagation;
import com.groupg.cells2d.engine.SimulationEngine;
import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.ParisGridFactory;
import com.groupg.cells2d.model.board.SimulationParams;
import com.groupg.cells2d.model.enums.AgeGroup;
import com.groupg.cells2d.model.user.PatientCase;
import com.groupg.cells2d.model.user.PatientCaseRepo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DoctorInterfaceController extends AbstractSimController implements Initializable {

    // --- Formulaire patient ---
    @FXML private TextField          caseIdField;
    @FXML private ComboBox<AgeGroup> ageGroupCombo;
    @FXML private TextField          symptomsField;
    @FXML private TextField          caseCountField;
    @FXML private Label              selectedCellLabel;

    // --- Controls ---
    @FXML private Button btnPlay;
    @FXML private Button btnPause;
    @FXML private Slider speedSlider;
    @FXML private Label  speedLabel;

    // --- State ---
    private Cell selectedCell = null;
    private int  nextCaseId   = 1;

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ageGroupCombo.getItems().addAll(AgeGroup.ADULT, AgeGroup.CHILD, AgeGroup.ELDERY);

        SimulationEngine loaded = SaveManager.autoLoad();
        if (loaded != null) {
            engine    = loaded;
            parisGrid = engine.getGrid();
        } else {
            parisGrid = ParisGridFactory.createDefaultParisGrid();
            SimulationParams covidParams = new SimulationParams(0.25, 0.2, 0.1, 0.15, 0.02);
            covidParams.setXi(0.02);
            engine = new SimulationEngine(parisGrid, new Propagation(covidParams));
        }

        engine.setStepDuration(1000);

        speedSlider.setValue(engine.getStepDuration());
        speedSlider.valueProperty().addListener((obs, o, n) -> {
            engine.setStepDuration(n.intValue());
            speedLabel.setText(String.format("Vitesse : %d ms/step", n.intValue()));
        });
        speedLabel.setText(String.format("Vitesse : %d ms/step", engine.getStepDuration()));

        updateCaseIdField();
        updateStatistics();
        updateStatusUI();

        mapImageView.setImage(new Image(
            getClass().getResourceAsStream(ParisGridFactory.MAP_RESOURCE)));

        mapPane.widthProperty().addListener((obs, o, n) -> drawMap());
        mapPane.heightProperty().addListener((obs, o, n) -> drawMap());
        drawMap();
    }

    // -------------------------------------------------------------------------
    // Hook : clic sur cellule (sélection pour formulaire)
    // -------------------------------------------------------------------------

    @Override
    protected void onCellClicked(Cell cell) {
        selectedCell = cell;
        selectedCellLabel.setText(
            "✔ " + cell.getCellId() + " | " + cell.getDistrictName()
            + " | état : " + cell.getState()
            + " | pop : "  + cell.getPopulation());
        drawGrid();
    }

    @Override
    protected Color getCellStrokeColor(Cell cell) {
        if (!cell.isAlive())      return Color.rgb(255, 0,   0,   0.85);
        if (cell == selectedCell) return Color.rgb(0,   120, 255, 1.0);
        return Color.rgb(20, 20, 20, 0.22);
    }

    @Override
    protected double getCellStrokeWidth(Cell cell) {
        if (!cell.isAlive())      return 3.0;
        if (cell == selectedCell) return 2.5;
        return 0.35;
    }

    // -------------------------------------------------------------------------
    // onPause override : autoSave
    // -------------------------------------------------------------------------

    @Override
    public void onPause() {
        super.onPause();
        SaveManager.autoSave(engine);
    }

    // -------------------------------------------------------------------------
    // Formulaire patient
    // -------------------------------------------------------------------------

    private void updateCaseIdField() {
        caseIdField.setText(String.valueOf(nextCaseId));
    }

    @FXML
    public void submitCase(ActionEvent event) {
        if (selectedCell == null) {
            selectedCellLabel.setText("⚠ Veuillez d'abord sélectionner une cellule sur la carte.");
            return;
        }
        AgeGroup age = ageGroupCombo.getSelectionModel().getSelectedItem();
        if (age == null) {
            selectedCellLabel.setText("⚠ Veuillez sélectionner un groupe d'âge.");
            return;
        }

        List<String> symptoms = new ArrayList<>(
            List.of(symptomsField.getText().isBlank() ? new String[]{""} : symptomsField.getText().split(",")));

        int count = 1;
        try {
            count = Integer.parseInt(caseCountField.getText().trim());
            if (count < 1) count = 1;
        } catch (NumberFormatException ignored) {}

        try {
            PatientCaseRepo.addCase(new PatientCase(nextCaseId, age, symptoms, "", selectedCell.getCellId()));
        } catch (Exception e) {
            e.printStackTrace();
            selectedCellLabel.setText("⚠ Erreur lors de l'ajout du cas.");
            return;
        }

        // Transfère N personnes S → I
        com.groupg.cells2d.model.board.SEIRData sd = selectedCell.getSeirData();
        double transferable = Math.min(count, sd.getSusceptible());
        sd.setSusceptible(sd.getSusceptible() - transferable);
        sd.setInfected(sd.getInfected() + transferable);
        double infectionRate = selectedCell.getPopulation() > 0
            ? sd.getInfected() / selectedCell.getPopulation() : 0;
        selectedCell.updateState(infectionRate);

        nextCaseId++;
        updateCaseIdField();
        symptomsField.clear();
        caseCountField.clear();
        selectedCell = null;
        selectedCellLabel.setText("✔ Cas ajouté. Cliquez sur la carte pour le suivant.");
        drawGrid();

        updateStatistics();
        SaveManager.autoSave(engine);
    }
}
