package com.groupg.cells2d.data;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * AES encryption of patient data, required for GDPR compliance.
 */
public class EncryptionService {

    private static final SecretKey MASTER_KEY;

    static {
        try {
            MASTER_KEY = loadMasterKey();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private EncryptionService() {} // utility class — not instantiable

    /**
     * Loads the AES master key from the {@code ENCRYPTION_KEY} environment variable.
     * The variable must contain a Base64-encoded 128/192/256-bit AES key.
     * @return the loaded secret key
     * @throws IllegalAccessException if the environment variable is not set
     */
    private static SecretKey loadMasterKey() throws IllegalAccessException {
        String encodedKey = System.getenv("ENCRYPTION_KEY");
        if (encodedKey == null) {
            throw new IllegalAccessException("ENCRYPTION_KEY environment variable not set");
        }
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    /**
     * Encrypts a plain-text string with AES in ECB mode and returns a Base64-encoded result.
     * @param string the plain text to encrypt
     * @param key    the AES secret key
     * @return Base64-encoded cipher text
     */
    public static String AESEncryption(String string, SecretKey key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
                   InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(string.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Decrypts a Base64-encoded AES cipher text and returns the original plain text.
     * @param encryptedString Base64-encoded cipher text
     * @param key             the AES secret key
     * @return decrypted plain text
     */
    public static String AESDecryption(String encryptedString, SecretKey key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
                   InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedString);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted);
    }

    /**
     * Encrypts a string using the application master key.
     * @param string the plain text to encrypt
     * @return Base64-encoded cipher text
     */
    public static String encrypt(String string)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
                   InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return AESEncryption(string, MASTER_KEY);
    }

    /**
     * Decrypts a cipher text using the application master key.
     * @param string Base64-encoded cipher text
     * @return decrypted plain text
     */
    public static String decrypt(String string)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
                   InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return AESDecryption(string, MASTER_KEY);
    }
}
