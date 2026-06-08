package com.groupg.cells2d.data;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

public class PasswordHash {
    private final byte[] hash;
    private final byte[] salt;
    private final int iterations =990;
    private final int keyLength = 256;
    public PasswordHash(String password) throws Exception{
        this.salt = generateSalt();
        this.hash = hashPassword(password,this.salt,iterations,keyLength);
    }
    /**
     *
     * @param password
     * @param salt
     * @param iterations
     * @param keyLength
     * @return
     * @throws Exception
     *
     *password hashing method using PBKDF2 algorithm
     *
     */

    private static byte[] hashPassword(String password,byte[] salt, int iterations, int keyLength ) throws Exception{
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),salt, iterations,keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();

    }
    private static byte[] generateSalt(){
        SecureRandom random = new SecureRandom();
        byte salt[]  = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public boolean verifyPassword(String candidatePassword) throws Exception {
        byte[] canditateHash = hashPassword(candidatePassword,this.salt,this.iterations,keyLength);
        return Arrays.equals(this.hash,canditateHash);
    }
}
