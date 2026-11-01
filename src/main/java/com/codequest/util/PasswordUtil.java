package com.codequest.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Module 2 / 3 / 4 - Registration and Login
 *
 * Converts a plain password into a SHA-256 hash (64 lowercase hex
 * characters). Only the hash is stored in the database, never the
 * real password. MySQL's SHA2('text', 256) gives the same value.
 */
public class PasswordUtil {

    public static String hash(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plain.getBytes(StandardCharsets.UTF_8));

            // Convert each byte into two hex characters
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is always available in Java, so this should never happen
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
