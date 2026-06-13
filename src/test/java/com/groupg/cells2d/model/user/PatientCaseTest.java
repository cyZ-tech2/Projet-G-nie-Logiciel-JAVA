package com.groupg.cells2d.model.user;

import com.groupg.cells2d.data.EncryptionService;
import com.groupg.cells2d.data.GsonManager;
import com.groupg.cells2d.data.JsonRepository;
import com.groupg.cells2d.model.enums.AgeGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import javax.crypto.KeyGenerator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SystemStubsExtension.class)
class PatientCaseTest {

    @SystemStub
    private static EnvironmentVariables envVars = new EnvironmentVariables("ENCRYPTION_KEY", generateAesKey());

    /**
     * Génère une clé AES 256 bits encodée en Base64 pour les tests d'encryption.
     * @return Base64 AES key string
     */
    private static String generateAesKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256);
            return Base64.getEncoder().encodeToString(kg.generateKey().getEncoded());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies that encryption followed by decryption returns the original plaintext.
     * @throws Exception
     */
    @Test
    void encryptDecryptTest() throws Exception {
        String plain = "Ceci est un test!";
        String cipher = EncryptionService.encrypt(plain);

        assertNotNull(cipher);
        assertNotEquals(plain, cipher);

        assertEquals(plain, EncryptionService.decrypt(cipher));
    }

    /**
     * Teste le chargement -> décryptage -> encryption -> sauvegarde des données
     * @param tmp
     * @throws Exception
     */
    @Test
    void saveAndLoadTest(@TempDir Path tmp) throws Exception {
        GsonManager gson = new GsonManager();
        Path file = tmp.resolve("cases.enc");

        JsonRepository<PatientCase> repo = new JsonRepository<>(gson, PatientCase.class, file.toString());

        new PatientCase(1.0, AgeGroup.ADULT, List.of("fever", "cough"), "Flu", "cell_3_5").encryptAndSave(repo);

        assertTrue(Files.exists(file));
        assertTrue(Files.size(file) > 0);

        JsonRepository<PatientCase> reader = new JsonRepository<>(gson, PatientCase.class, file.toString());
        PatientCase.loadAndDecrypt(reader);

        Set<PatientCase> loaded = reader.getAll();
        assertNotNull(loaded);
        assertEquals(1, loaded.size());
    }

    /**
     * Vérifie que plusieurs sauvegardes s'ajoutent au lieu de s'écraser.
     * @param tmp Fichier temporaire
     * @throws Exception
     */
    @Test
    void twoSavesAccumulate(@TempDir Path tmp) throws Exception {
        GsonManager gson = new GsonManager();
        Path file = tmp.resolve("many.enc");
        JsonRepository<PatientCase> repo = new JsonRepository<>(gson, PatientCase.class, file.toString());

        new PatientCase(10.0, AgeGroup.CHILD, List.of("sneezing"), "Cold", "cell_0_0").encryptAndSave(repo);
        new PatientCase(20.0, AgeGroup.ELDERY, List.of("wheezing"), "Asthma", "cell_1_1").encryptAndSave(repo);

        JsonRepository<PatientCase> reader = new JsonRepository<>(gson, PatientCase.class, file.toString());
        PatientCase.loadAndDecrypt(reader);

        assertEquals(2, reader.getAll().size());
    }

    /**
     * L'ouverture d'un fichier vide ne doit pas renvoyer d'erreur
     * @param tmp Fichier temporaire
     */
    @Test
    void missingFileDoesNotThrow(@TempDir Path tmp) {
        GsonManager gson = new GsonManager();
        Path missing = tmp.resolve("ghost.enc");
        JsonRepository<PatientCase> repo = new JsonRepository<>(gson, PatientCase.class, missing.toString());

        assertDoesNotThrow(() -> PatientCase.loadAndDecrypt(repo));
        assertTrue(repo.getAll().isEmpty());
    }

    /**
     * Le fichier doit bien être encrypté
     * @param tmp
     * @throws Exception Fichier temporaire
     */
    @Test
    void savedFileIsEncrypted(@TempDir Path tmp) throws Exception {
        GsonManager gson = new GsonManager();
        Path file = tmp.resolve("secret.enc");
        JsonRepository<PatientCase> repo = new JsonRepository<>(gson, PatientCase.class, file.toString());

        new PatientCase(99.0, AgeGroup.ELDERY, List.of("headache", "dizziness"), "Migraine", "cell_7_2").encryptAndSave(repo);

        String raw = Files.readString(file);

        assertFalse(raw.contains("Migraine"));
        assertFalse(raw.contains("headache"));
        assertFalse(raw.contains("dizziness"));
        assertFalse(raw.contains("cell_7_2"));
    }
}
