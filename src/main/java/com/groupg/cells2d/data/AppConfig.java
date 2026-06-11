package com.groupg.cells2d.data;

import com.groupg.cells2d.model.user.Doctor;
import com.groupg.cells2d.model.user.PatientCase;
import com.groupg.cells2d.model.user.Researcher;
import com.groupg.cells2d.model.user.User;


import java.util.Map;

public class AppConfig {
    /**
     * Saves the hierarchy of each classes  their types can be indicated when
     * converted to Json
     */
    public static final GsonManager GSON_MANAGER = new GsonManager()

            // User hierarchy
            .registerHierarchy(User.class, Map.of(
                    "docteur",   Doctor.class,
                    "chercheur", Researcher.class
            ))

             //Patient hierarchy
            .registerHierarchy(PatientCase.class,Map.of(
                    "patientCase", PatientCase.class
            ));

}
