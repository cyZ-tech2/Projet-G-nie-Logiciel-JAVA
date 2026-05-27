package com.groupg.cells2d.model.user;

import com.groupg.cells2d.data.SaveJson;

/**
 * classe décrivant un médecin
 */

public class Doctor extends User {

    private String location;
    private String speciality;

    public Doctor(double id, String username, String password,String location, String speciality) throws Exception {
        super(id, username, password);
        this.location = location;
        this.speciality = speciality;
        SaveJson.save(this,this.getClass().getResource("dataBase/users.json"));
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }
}
