package com.groupg.cells2d.model.user;

import com.groupg.cells2d.model.enums.AgeGroup;

import java.util.List;

/**
 * rajouter un cas de patient
 */

public class PatientCase {
    private double caseid;
    private AgeGroup ageGroup;
    private List<String> symptoms;
    private String suspectedDisease;
    private double cellid;


    public PatientCase(double caseid, AgeGroup ageGroup, List<String> symptoms, String suspectedDisease, double cellid) {
        this.caseid = caseid;
        this.ageGroup = ageGroup;
        this.symptoms = symptoms;
        this.suspectedDisease = suspectedDisease;
        this.cellid = cellid;
    }

}
