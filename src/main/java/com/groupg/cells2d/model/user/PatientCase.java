package com.groupg.cells2d.model.user;

import com.groupg.cells2d.data.EncryptionService;
import com.groupg.cells2d.data.JsonRepository;
import com.groupg.cells2d.model.enums.AgeGroup;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * rajouter un cas de patient
 */

public class PatientCase {
    private double caseid;
    private AgeGroup ageGroup;
    private List<String> symptoms;
    private String suspectedDisease;
    private String cellid;


    public PatientCase(double caseid, AgeGroup ageGroup, List<String> symptoms, String suspectedDisease, String cellid) {
        this.caseid = caseid;
        this.ageGroup = ageGroup;
        this.symptoms = symptoms;
        this.suspectedDisease = suspectedDisease;
        this.cellid = cellid;
    }

    /**
     * Deserializes database, adds the case and Serializes file into json, encrypts and saves it into a file
     * @param patientCaseRepo
     * @throws IOException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public void encryptAndSave(JsonRepository<PatientCase> patientCaseRepo) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        loadAndDecrypt(patientCaseRepo);
        patientCaseRepo.add(this);
        Files.writeString(patientCaseRepo.getFilePath(), EncryptionService.encrypt((patientCaseRepo.getJson())));
    }

    public  static void loadAndDecrypt(JsonRepository<PatientCase> patientCaseRepo) throws IOException,NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException{
        if(!Files.exists(patientCaseRepo.getFilePath())) return;
        patientCaseRepo.load(EncryptionService.decrypt(Files.readString(patientCaseRepo.getFilePath())));

    }


}
