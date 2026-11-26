package com.fincore.util;

import java.security.SecureRandom;

/**
 * Builds random reference numbers such as ACC482910374615. Timestamps collide when two
 * requests arrive in the same millisecond; 12 random digits make that vanishingly rare,
 * and callers still check uniqueness against the database.
 */
public final class ReferenceGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    private ReferenceGenerator() {
    }

    public static String next(String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 12; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}
