package com.thyn.backend.utilities.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;

import com.thyn.backend.ThyNConstants;
import com.thyn.backend.api.APIErrorException;
import com.thyn.backend.entities.users.User;
/**
 * Created by shalu on 3/4/16.
 */
public class SecurityUtilities {
    public static byte[] getRandomSalt() throws NoSuchAlgorithmException
    {
        SecureRandom rGen = SecureRandomGenerator.getInstance();
        byte[] salt = new byte[ThyNConstants.SALT_BYTES_NUMBER];
        rGen.nextBytes(salt);
        return salt;
    }

    public static String computesHashFromPassword(String password, byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException
    {
        if (salt == null || salt.length < ThyNConstants.SALT_BYTES_NUMBER)
            throw new IllegalArgumentException("Salt is null or is not "+ThyNConstants.SALT_BYTES_NUMBER+" byte long.");
        if (password == null || password.trim().length() == 0)
            throw new IllegalArgumentException("Password is null or is empty.");

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey key = factory.generateSecret(spec);
        return new String(Base64.encodeBase64(key.getEncoded()));
    }
    public static ThyNSession enforceAuthenticationForCurrentAPICall(HttpServletRequest req) throws APIErrorException
    {
        ThyNSession currentSession = ThyNSession.getSession(req);

        if (currentSession == null)
            throw new APIErrorException(401, "SECE01 - Unauthorized.");

        User sessionUser = currentSession.getSessionUser();
        if (sessionUser == null )
            throw new APIErrorException(401, "SECE02 - Unauthorized.");

        return currentSession;
    }
}
