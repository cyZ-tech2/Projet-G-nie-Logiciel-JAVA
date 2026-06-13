package com.groupg.cells2d.model.user;

import com.groupg.cells2d.data.AppConfig;
import com.groupg.cells2d.data.JsonRepository;
import com.groupg.cells2d.data.PasswordHash;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la hiérarchie {@link User}, incluant le hachage de mot de passe,
 * la construction de {@link Doctor} et {@link Researcher}, la vérification du hashcode,
 * l'authentification via {@link User#login} et la récupération via {@link User#get}.
 */
class UserTest {
    /**
     * Vérifie la validation d'un mot de passe
     * @throws Exception erreur PasswordHash
     */
    @Test
    void passwordVerify() throws Exception {
        PasswordHash hash = new PasswordHash("s3cret");
        assertTrue(hash.verifyPassword("s3cret"));
        assertFalse(hash.verifyPassword("wrong"));

    }

    /**
     * Vérifie la construction correcte d'un {@link Doctor}.
     * @throws Exception erreur PasswordHash
     */
    @Test
    void doctorConstructedCorrectly() throws Exception {
        Doctor d = new Doctor(1.0, "doc", "pass", "Paris", "cardiology");

        assertEquals(1.0, d.getId());
        assertEquals("doc", d.getUsername());
        assertEquals("Paris", d.getLocation());
        assertEquals("cardiology", d.getSpeciality());
    }

    /**
     * Vérifie la construction correcte d'un {@link Researcher} avec tous ses champs.
     * @throws Exception erreur PasswordHash
     */
    @Test
    void researcherTest() throws Exception {
        Researcher r = new Researcher(2.0, "r", "pass", "Institut Cochin");

        assertEquals(2.0, r.getId());
        assertEquals("r", r.getUsername());
        assertEquals("Institut Cochin", r.getInstitution());
    }

    /**
     * Vérifie que l'égalité est basée sur l'id et le nom d'utilisateur,
     * indépendamment des autres champs.
     * @throws Exception erreur PasswordHash
     */
    @Test
    void hashCodeEqualsTest() throws Exception {
        Doctor a = new Doctor(1.0, "doc", "x", "Paris", "cardio");
        Doctor b = new Doctor(1.0, "doc", "y", "Lyon",  "neuro");
        Doctor c = new Doctor(2.0, "oth", "z", "Paris", "cardio");
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a, b);
        assertNotEquals(a, c);
    }

    /**
     * Vérifie que l'authentification échoue pour un utilisateur inexistant
     * @throws Exception erreur PasswordHash
     */
    @Test
    void unknownUserTest() throws Exception {
        assertFalse(User.login("nobody", "anything"));
        assertNull(User.get("nobody"));
    }
}