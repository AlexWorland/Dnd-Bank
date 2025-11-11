package com.dndbank.bank.dto;

import com.dndbank.bank.support.CurrencyUtils;
import jakarta.validation.constraints.Min;

public class MoneyAmountForm {
    @Min(0)
    private Integer platinum;

    @Min(0)
    private Integer gold;

    @Min(0)
    private Integer electrum;

    @Min(0)
    private Integer silver;

    @Min(0)
    private Integer copper;

    public Integer getPlatinum() {
        return platinum;
    }

    public void setPlatinum(Integer platinum) {
        this.platinum = platinum;
    }

    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }

    public Integer getElectrum() {
        return electrum;
    }

    public void setElectrum(Integer electrum) {
        this.electrum = electrum;
    }

    public Integer getSilver() {
        return silver;
    }

    public void setSilver(Integer silver) {
        this.silver = silver;
    }

    public Integer getCopper() {
        return copper;
    }

    public void setCopper(Integer copper) {
        this.copper = copper;
    }

    public long toCopper() {
        return CurrencyUtils.toCopper(valueOf(platinum), valueOf(gold), valueOf(electrum), valueOf(silver), valueOf(copper));
    }

    public boolean hasPositiveValue() {
        return toCopper() > 0;
    }

    public boolean isProvided() {
        return platinum != null || gold != null || electrum != null || silver != null || copper != null;
    }

    private long valueOf(Integer value) {
        return value == null ? 0L : value;
    }
}
