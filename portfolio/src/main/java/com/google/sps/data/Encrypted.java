package com.google.sps.data;

import java.security.SecureRandom;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

public class Encrypted<T> {
    String sha256Hex;
    byte[] salt;

    public Encrypted(T value) {
        salt = generateSalt();
        sha256Hex = hashWithSalt(value);
    }

    public Encrypted() {}

    public boolean matches(T value) {
        return hashWithSalt(value).equals(sha256Hex);
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        rng.nextBytes(salt);
        return salt;
    }

    private String hashWithSalt(T value) {
        return DigestUtils.sha256Hex(ArrayUtils.addAll(salt, value.toString().getBytes()));
    }

    private static SecureRandom rng = new SecureRandom();
}
