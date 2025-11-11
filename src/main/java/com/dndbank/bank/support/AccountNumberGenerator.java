package com.dndbank.bank.support;

import java.security.SecureRandom;

public final class AccountNumberGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    private AccountNumberGenerator() {
    }

    public static String nextAccountNumber() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            builder.append(RANDOM.nextInt(10));
        }
        return builder.toString();
    }
}
