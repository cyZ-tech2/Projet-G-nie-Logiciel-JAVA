package com.groupg.cells2d.controller;

import com.groupg.cells2d.model.enums.AgeGroup;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class DoctorInterfaceController implements Initializable {
    @FXML
    private ComboBox<AgeGroup> ageGroupCombo;

    private AgeGroup[] ageGroup = {AgeGroup.ADULT,AgeGroup.CHILD,AgeGroup.ELDERY};

    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        ageGroupCombo.getItems().addAll(ageGroup);
    }
}
