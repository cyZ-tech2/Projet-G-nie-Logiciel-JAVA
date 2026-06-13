package com.groupg.cells2d.data;

import com.groupg.cells2d.model.user.Doctor;
import com.groupg.cells2d.model.user.PatientCase;
import com.groupg.cells2d.model.user.Researcher;
import com.groupg.cells2d.model.user.User;

import java.util.Map;

/**
 * Application-wide configuration constants.
 * Initialises the shared {@link GsonManager} with all polymorphic type hierarchies
 * so that {@link com.groupg.cells2d.data.JsonRepository} instances can
 * serialise and deserialise concrete sub-types correctly.
 */
public class AppConfig {
    /**
     * Shared Gson manager pre-configured with the User and PatientCase type hierarchies.
     * Used by all JsonRepository instances in the application.
     */
    public static final GsonManager GSON_MANAGER = new GsonManager()
        .registerHierarchy(User.class, Map.of(
            "docteur",   Doctor.class,
            "chercheur", Researcher.class
        ))
        .registerHierarchy(PatientCase.class, Map.of(
            "patientCase", PatientCase.class
        ));
}
