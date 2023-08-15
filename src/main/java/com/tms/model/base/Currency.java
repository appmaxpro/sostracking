package com.tms.model.base;

import com.tms.model.Model;

public class Currency extends Model {

    private String name;
    private String code;
    private String symbol;
    private int decimalPlaces = 0;
    private boolean positionBefore ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public boolean isPositionBefore() {
        return positionBefore;
    }

    public void setPositionBefore(boolean positionBefore) {
        this.positionBefore = positionBefore;
    }
}
