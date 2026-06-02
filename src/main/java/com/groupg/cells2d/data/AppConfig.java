package com.groupg.cells2d.data;

import com.groupg.cells2d.model.user.Doctor;
import com.groupg.cells2d.model.user.Researcher;
import com.groupg.cells2d.model.user.User;

import java.util.Map;

public class AppConfig {

    public static final SaveJson GSON_MANAGER = new SaveJson()

            // Hiérarchie User
            .registerHierarchy(User.class, Map.of(
                    "docteur",   Doctor.class,
                    "chercheur", Researcher.class
            ));

            // Hiérarchie Patient
//            .registerHierarchy(Patient.class, Map.of(
//                    "adulte",   PatientAdulte.class,
//                    "pediatrique", PatientPediatrique.class
//            ));

    // Ajouter d'autres hiérarchies ici autant que nécessaire...
}
