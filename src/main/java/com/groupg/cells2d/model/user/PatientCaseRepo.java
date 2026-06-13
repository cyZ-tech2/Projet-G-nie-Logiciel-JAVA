package com.groupg.cells2d.model.user;

import com.groupg.cells2d.data.AppConfig;
import com.groupg.cells2d.data.JsonRepository;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class PatientCaseRepo {

    public static final JsonRepository<PatientCase> patientCaseRepo = new JsonRepository<>(
        AppConfig.GSON_MANAGER, PatientCase.class,
        "src/main/java/com/groupg/cells2d/model/user/data/patients.txt");

    public static void addCase(PatientCase patientCase)
            throws NoSuchPaddingException, IllegalBlockSizeException, IOException,
                   NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        patientCase.encryptAndSave(patientCaseRepo);
    }
}
