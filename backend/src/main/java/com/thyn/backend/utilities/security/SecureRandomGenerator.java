package com.thyn.backend.utilities.security;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Generator for cryptographic randomness.
 *
 * @author Angelo Agatino Nicolosi
 */
public class SecureRandomGenerator {
    private static SecureRandom randomGenerator;

    public static SecureRandom getInstance() throws NoSuchAlgorithmException
    {
        if (randomGenerator == null)
            randomGenerator = SecureRandom.getInstance("SHA1PRNG");
        return randomGenerator;
    }

    public static String nextPassword() throws NoSuchAlgorithmException
    {
        SecureRandom random = SecureRandomGenerator.getInstance();
        return new BigInteger(130, random).toString(32);
    }

}
