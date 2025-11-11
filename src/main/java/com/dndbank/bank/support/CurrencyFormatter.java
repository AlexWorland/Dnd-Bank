package com.dndbank.bank.support;

import org.springframework.stereotype.Component;

@Component("currency")
public class CurrencyFormatter {

    public CurrencyBreakdown breakdown(long copperAmount) {
        return CurrencyUtils.breakdown(copperAmount);
    }

    public String format(long copperAmount) {
        return CurrencyUtils.format(copperAmount);
    }
}
