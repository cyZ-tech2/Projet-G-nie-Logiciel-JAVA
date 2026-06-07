package com.groupg.cells2d.data;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * AES chiffrement des informations sur le patiens, nécessaire pour le respect du RGPD
 */

public class EncryptionService  {
private static final SecretKey MASTER_KEY;

    static {
        try {
            MASTER_KEY = loadMasterKey();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the master encryption key from environment variable
     * given that environement variable was set as such : ENCRYTPION_KEY = key
     * @return
     * @throws IllegalAccessException
     */
    private static SecretKey loadMasterKey() throws IllegalAccessException {
    String encodedKey = System.getenv("ENCRYPTION_KEY"); //get key from environement variables
    if(encodedKey == null){
        throw new IllegalAccessException("ENCRYPTION_KEY environement variable not set");
    }
    return new SecretKeySpec(encodedKey.getBytes(StandardCharsets.UTF_8),0,encodedKey.length(),"AES");
}

    /**
     * Encrypts using the key
     * @param string
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String AESEncryption(String string,SecretKey key) throws NoSuchAlgorithmException,NoSuchPaddingException,InvalidKeyException,BadPaddingException,IllegalBlockSizeException{
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] encrypted = cipher.doFinal(string.getBytes());
        return Arrays.toString(encrypted);
    }

    /**
     * Decrypt a String using the key
     * @param encryptedString
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String AESDecryption (String encryptedString, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException,InvalidKeyException,BadPaddingException,IllegalBlockSizeException{
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,key);

        return new String(cipher.doFinal(encryptedString.getBytes()));
    }

    /**
     * Encrypts a string using AES
     * @param string
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String encrypt(String string) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,IllegalBlockSizeException{
        return AESEncryption(string,MASTER_KEY);
    }

    /**
     * Decrypts a string using AES
     * @param string
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String decrypt(String string) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,IllegalBlockSizeException{
        return AESDecryption(string,MASTER_KEY);
    }




}
