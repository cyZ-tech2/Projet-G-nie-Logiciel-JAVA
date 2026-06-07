package com.groupg.cells2d.model.user;
import com.groupg.cells2d.data.AppConfig;
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

public class PatientCaseTest {
    public static void main(String[] args) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException {
        //System.out.println(EncryptionService.decrypt("wVuOYuLCXxU+OnkYE152y/GGzmUe0uyLAI/QyJwUwyE="));
        JsonRepository<PatientCase> patientCaseRepo = new JsonRepository<>(AppConfig.GSON_MANAGER,PatientCase.class,"src/main/java/com/groupg/cells2d/model/user/data/patients.txt");
        List<String> symptoms = List.of("Cough","Headache"  );
        PatientCase patient = new PatientCase(2, AgeGroup.ADULT,symptoms,"Covid-19",19);
//      //;
//        Files.writeString(patientCaseRepo.getFilePath(), EncryptionService.encrypt(patientCaseRepo.getJson()));
        patient.encryptAndSave(patientCaseRepo);
    }
}
