package com.groupg.cells2d.controller;

import com.groupg.cells2d.model.enums.AgeGroup;
import com.groupg.cells2d.model.user.PatientCase;
import com.groupg.cells2d.model.user.PatientCaseRepo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DoctorInterfaceController implements Initializable {
    @FXML
    private ComboBox<AgeGroup> ageGroupCombo;
    @FXML
    TextField symptomsField;
    @FXML
    TextField diseaseField;
    @FXML
    TextField cellIdField;
    @FXML
    TextField caseIdField;

    private AgeGroup[] ageGroup = {AgeGroup.ADULT,AgeGroup.CHILD,AgeGroup.ELDERY};

    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        ageGroupCombo.getItems().addAll(ageGroup);
    }

    public void submitCase (ActionEvent event) throws Exception,NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        AgeGroup ageGroup = ageGroupCombo.getSelectionModel().getSelectedItem();
        List<String> symptoms = new ArrayList<>();
        symptoms.addAll(List.of(symptomsField.getText().split(",")));
        String cellid = cellIdField.getText();
        double caseid = Double.parseDouble(caseIdField.getText());
        String suspectedDisease = diseaseField.getText();
        PatientCaseRepo.addCase(new PatientCase(caseid,ageGroup,symptoms,suspectedDisease,cellid));

    }
}
