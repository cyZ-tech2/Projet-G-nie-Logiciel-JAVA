package com.groupg.cells2d.model.user;

import com.groupg.cells2d.data.EncryptionService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Manual smoke test for the {@link PatientCase} encryption and persistence flow.
 * Not part of the automated test suite.
 */
public class PatientCaseTest {
    /**
     * Runs the manual patient-case smoke test.
     * @param args unused
     */
    public static void main(String[] args)
            throws NoSuchPaddingException, IllegalBlockSizeException,
                   NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        System.out.println(EncryptionService.decrypt(
            "xGAAcTbTl7WiEdfULMXjIYwmYRtCgfBKA79XzSwmsljCLFwRK2NOR7D88BI1Kl0XzNX5uL4Pn6ajuiiv52DGBs+Iw4SBZudKT9ok9aTjyMTi00rmO13gmxOW3NkMjJaGmbI/yd9fcEOy9ABJhKNd0TEizJQA+uonPzn2G9L5MINCL83bgiNh96BLh374XVeq3XrW4G7VDhpmjz7i0NCgg4rjQlgU0pQiw1l/Ybk7EOl+ELuw9F+5U7VwsTFbdLIgHlIgHiT2o//jN8/sYFDg3nD2LkYb/KbPNG1BSvWQF1uWTBYTBolBwlnjnsw3lsXYy6/TTTmp8icp7LE8krJQg2bFEUWAQTb4eFf1QaUxLTHE7YccTZVVCEDOZlurmGhkJvtUckHEACmHpomzF6KmgBmPp3e7HQRi7K/PrMRWPqKiuq4GCpo/HiLavkFulcFQ3qG+/VlG45TPrCagqbkQav426SjCK80Wam5lKf27d5uBehjS5PqYtZKp9B9TSzY7KuaN8vuKmfvHWwXDNuSSWWa6UK+NgP59JdIS7Dx0LerTF6weDQ37Gr7ojAtHEdGm01KU85A0oklxiSbG+7717A=="));
    }
}
