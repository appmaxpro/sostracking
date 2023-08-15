package com.tms.model;

import com.tms.model.types.TransRateRuleFactor;
import com.tms.model.types.TransRateRuleOperator;
import com.tms.model.types.TransRateRuleVariable;

import java.math.BigDecimal;

public class TransRateRule extends Model{
    private int sequence = 0;
    private long transPricing;
    private boolean accumulate;
    private boolean split;
    private TransRateRuleVariable variable;
    private TransRateRuleOperator op;
    private BigDecimal input;
    private TransRateRuleFactor factor;
    private BigDecimal multiple;

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public long getTransPricing() {
        return transPricing;
    }

    public void setTransPricing(long transPricing) {
        this.transPricing = transPricing;
    }

    public boolean isAccumulate() {
        return accumulate;
    }

    public void setAccumulate(boolean accumulate) {
        this.accumulate = accumulate;
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

    public TransRateRuleVariable getVariable() {
        return variable;
    }

    public void setVariable(TransRateRuleVariable variable) {
        this.variable = variable;
    }

    public TransRateRuleOperator getOp() {
        return op;
    }

    public void setOp(TransRateRuleOperator op) {
        this.op = op;
    }

    public BigDecimal getInput() {
        return input;
    }

    public void setInput(BigDecimal input) {
        this.input = input;
    }

    public TransRateRuleFactor getFactor() {
        return factor;
    }

    public void setFactor(TransRateRuleFactor factor) {
        this.factor = factor;
    }

    public BigDecimal getMultiple() {
        return multiple;
    }

    public void setMultiple(BigDecimal multiple) {
        this.multiple = multiple;
    }
}
