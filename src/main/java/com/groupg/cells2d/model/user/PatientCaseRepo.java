package com.groupg.cells2d.model.user;

import com.groupg.cells2d.data.AppConfig;
import com.groupg.cells2d.data.JsonRepository;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Singleton-style repository for {@link PatientCase} records.
 * Wraps a {@link com.groupg.cells2d.data.JsonRepository} backed by the
 * encrypted patients file and exposes a single static entry point to add cases.
 */
public class PatientCaseRepo {

    /** The shared encrypted repository used to persist all patient cases. */
    public static final JsonRepository<PatientCase> patientCaseRepo = new JsonRepository<>(
        AppConfig.GSON_MANAGER, PatientCase.class,
        "src/main/java/com/groupg/cells2d/model/user/data/patients.txt");

    /**
     * Adds a patient case to the repository and immediately encrypts and saves the file.
     * @param patientCase the case to persist
     * @throws NoSuchPaddingException    if the cipher is unavailable
     * @throws IllegalBlockSizeException if encryption fails
     * @throws IOException               if the file cannot be written
     * @throws NoSuchAlgorithmException  if AES is unavailable
     * @throws BadPaddingException       if the padding is incorrect
     * @throws InvalidKeyException       if the encryption key is invalid
     */
    public static void addCase(PatientCase patientCase)
            throws NoSuchPaddingException, IllegalBlockSizeException, IOException,
                   NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        patientCase.encryptAndSave(patientCaseRepo);
    }
}
