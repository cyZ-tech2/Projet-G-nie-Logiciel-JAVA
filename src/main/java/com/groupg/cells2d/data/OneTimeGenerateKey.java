package com.groupg.cells2d.data;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class OneTimeGenerateKey {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey key = keyGen.generateKey();
        System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
    }
}
