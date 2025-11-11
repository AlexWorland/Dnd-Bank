package com.dndbank.bank.support;

public final class CurrencyUtils {
    public static final long COPPER_PER_SILVER = 10L;
    public static final long COPPER_PER_GOLD = COPPER_PER_SILVER * 10L; // 100
    public static final long COPPER_PER_ELECTRUM = COPPER_PER_GOLD / 2L; // 50
    public static final long COPPER_PER_PLATINUM = COPPER_PER_GOLD * 10L; // 1000

    private CurrencyUtils() {
    }

    public static long toCopper(long platinum, long gold, long electrum, long silver, long copper) {
        return platinum * COPPER_PER_PLATINUM
                + gold * COPPER_PER_GOLD
                + electrum * COPPER_PER_ELECTRUM
                + silver * COPPER_PER_SILVER
                + copper;
    }

    public static CurrencyBreakdown breakdown(long copperAmount) {
        long remaining = Math.abs(copperAmount);
        long platinum = remaining / COPPER_PER_PLATINUM;
        remaining %= COPPER_PER_PLATINUM;
        long gold = remaining / COPPER_PER_GOLD;
        remaining %= COPPER_PER_GOLD;
        long electrum = remaining / COPPER_PER_ELECTRUM;
        remaining %= COPPER_PER_ELECTRUM;
        long silver = remaining / COPPER_PER_SILVER;
        remaining %= COPPER_PER_SILVER;
        return new CurrencyBreakdown(platinum, gold, electrum, silver, remaining);
    }

    public static String format(long copperAmount) {
        boolean negative = copperAmount < 0;
        long absolute = Math.abs(copperAmount);
        CurrencyBreakdown breakdown = breakdown(absolute);
        StringBuilder builder = new StringBuilder();
        if (negative) {
            builder.append("-");
        }
        appendUnit(builder, breakdown.platinum(), "pp");
        appendUnit(builder, breakdown.gold(), "gp");
        appendUnit(builder, breakdown.electrum(), "ep");
        appendUnit(builder, breakdown.silver(), "sp");
        appendUnit(builder, breakdown.copper(), "cp");
        if (builder.length() == 0 || (negative && builder.length() == 1)) {
            return negative ? "- 0 cp" : "0 cp";
        }
        return builder.toString().trim();
    }

    private static void appendUnit(StringBuilder builder, long value, String suffix) {
        if (value <= 0) {
            return;
        }
        builder.append(' ').append(value).append(' ').append(suffix);
    }
}
