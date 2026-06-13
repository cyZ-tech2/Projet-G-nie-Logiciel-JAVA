package com.groupg.cells2d.data;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * One-time utility that generates and prints a new Base64-encoded AES key.
 * Run once to create the key, then set it as the {@code ENCRYPTION_KEY}
 * environment variable. Do not commit the output to version control.
 */
public class OneTimeGenerateKey {
    /**
     * Generates a 256-bit AES key and prints it Base64-encoded to stdout.
     * @param args unused
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey key = keyGen.generateKey();
        System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
    }
}
