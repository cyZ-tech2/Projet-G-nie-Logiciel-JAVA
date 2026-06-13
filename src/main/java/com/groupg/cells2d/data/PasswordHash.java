package com.groupg.cells2d.data;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

/**
 * Hashes and verifies passwords using PBKDF2 with HMAC-SHA256.
 * A unique random salt is generated for each instance to prevent
 * rainbow-table attacks.
 */
public class PasswordHash {

    private final byte[] hash;
    private final byte[] salt;
    private final int iterations = 990;
    private final int keyLength  = 256;

    /**
     * Hashes the given password with a freshly generated random salt.
     * @param password the plain-text password to hash
     * @throws NoSuchAlgorithmException if PBKDF2 is unavailable
     * @throws InvalidKeySpecException  if the key specification is invalid
     */
    public PasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.salt = generateSalt();
        this.hash = hashPassword(password, this.salt, iterations, keyLength);
    }

    /**
     * Computes a PBKDF2-HMAC-SHA256 hash of the given password.
     * @param password   plain-text password
     * @param salt       random salt bytes
     * @param iterations number of PBKDF2 iterations
     * @param keyLength  desired key length in bits
     * @return the derived key bytes
     */
    private static byte[] hashPassword(String password, byte[] salt, int iterations, int keyLength)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }

    /**
     * Generates a cryptographically secure random 16-byte salt.
     * @return salt bytes
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Verifies a candidate password against the stored hash.
     * @param candidatePassword the plain-text password to verify
     * @return true if the password matches, false otherwise
     * @throws NoSuchAlgorithmException if PBKDF2 is unavailable
     * @throws InvalidKeySpecException  if the key specification is invalid
     */
    public boolean verifyPassword(String candidatePassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] candidateHash = hashPassword(candidatePassword, this.salt, this.iterations, keyLength);
        return Arrays.equals(this.hash, candidateHash);
    }
}
