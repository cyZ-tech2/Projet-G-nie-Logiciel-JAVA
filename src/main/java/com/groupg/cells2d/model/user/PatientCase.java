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
 * Represents a single patient case reported by a doctor.
 * Stores epidemiological metadata (age group, symptoms, suspected disease)
 * and the grid cell where the case was detected.
 * Patient data is encrypted before being persisted to disk.
 */
public class PatientCase {
    private double caseid;
    private AgeGroup ageGroup;
    private List<String> symptoms;
    private String suspectedDisease;
    private String cellid;


    /**
     * Creates a new patient case record.
     * @param caseid            unique case identifier
     * @param ageGroup          age group of the patient
     * @param symptoms          list of reported symptoms
     * @param suspectedDisease  suspected disease name (may be empty)
     * @param cellid            identifier of the grid cell where the case occurred
     */
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

    /**
     * Decrypts the patient data file and loads its contents into the repository.
     * Does nothing if the file does not exist.
     * @param patientCaseRepo the repository to populate
     * @throws IOException               if the file cannot be read
     * @throws NoSuchPaddingException    if the cipher is unavailable
     * @throws IllegalBlockSizeException if decryption fails
     * @throws NoSuchAlgorithmException  if AES is unavailable
     * @throws BadPaddingException       if the padding is incorrect
     * @throws InvalidKeyException       if the encryption key is invalid
     */
    public  static void loadAndDecrypt(JsonRepository<PatientCase> patientCaseRepo) throws IOException,NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException{
        if(!Files.exists(patientCaseRepo.getFilePath())) return;
        patientCaseRepo.load(EncryptionService.decrypt(Files.readString(patientCaseRepo.getFilePath())));

    }


}
