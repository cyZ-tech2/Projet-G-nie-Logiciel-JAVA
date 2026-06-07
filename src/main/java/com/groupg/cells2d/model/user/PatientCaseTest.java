package com.groupg.cells2d.model.user;
import com.groupg.cells2d.data.EncryptionService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class PatientCaseTest {
    public static void main(String[] args) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        System.out.println(EncryptionService.decrypt("wVuOYuLCXxU+OnkYE152y/GGzmUe0uyLAI/QyJwUwyE="));
    }
}
