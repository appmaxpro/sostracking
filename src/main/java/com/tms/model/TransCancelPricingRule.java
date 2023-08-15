package com.tms.model;

import com.tms.model.types.OrderRoleType;

import java.math.BigDecimal;

public class TransCancelPricingRule extends Model{
    private int sequence = 0;
    private long cancelPricing;
    private boolean applyAfterDistance;
    private boolean applyAfterDuration;
    private OrderRoleType deductionFrom;
    private OrderRoleType secondDeductionFrom;
    private BigDecimal deductionAmount;
    private BigDecimal secondDeductionAmount;
    private OrderRoleType beneficiaryRoleType;
    private OrderRoleType secondBeneficiaryRoleType;
    private BigDecimal firstAmount;
    private BigDecimal secondAmount;
    private boolean usePercentCharge;
    private double percentCharge;

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public long getCancelPricing() {
        return cancelPricing;
    }

    public void setCancelPricing(long cancelPricing) {
        this.cancelPricing = cancelPricing;
    }

    public boolean isApplyAfterDistance() {
        return applyAfterDistance;
    }

    public void setApplyAfterDistance(boolean applyAfterDistance) {
        this.applyAfterDistance = applyAfterDistance;
    }

    public boolean isApplyAfterDuration() {
        return applyAfterDuration;
    }

    public void setApplyAfterDuration(boolean applyAfterDuration) {
        this.applyAfterDuration = applyAfterDuration;
    }

    public OrderRoleType getDeductionFrom() {
        return deductionFrom;
    }

    public void setDeductionFrom(OrderRoleType deductionFrom) {
        this.deductionFrom = deductionFrom;
    }

    public OrderRoleType getSecondDeductionFrom() {
        return secondDeductionFrom;
    }

    public void setSecondDeductionFrom(OrderRoleType secondDeductionFrom) {
        this.secondDeductionFrom = secondDeductionFrom;
    }

    public BigDecimal getDeductionAmount() {
        return deductionAmount;
    }

    public void setDeductionAmount(BigDecimal deductionAmount) {
        this.deductionAmount = deductionAmount;
    }

    public BigDecimal getSecondDeductionAmount() {
        return secondDeductionAmount;
    }

    public void setSecondDeductionAmount(BigDecimal secondDeductionAmount) {
        this.secondDeductionAmount = secondDeductionAmount;
    }

    public OrderRoleType getBeneficiaryRoleType() {
        return beneficiaryRoleType;
    }

    public void setBeneficiaryRoleType(OrderRoleType beneficiaryRoleType) {
        this.beneficiaryRoleType = beneficiaryRoleType;
    }

    public OrderRoleType getSecondBeneficiaryRoleType() {
        return secondBeneficiaryRoleType;
    }

    public void setSecondBeneficiaryRoleType(OrderRoleType secondBeneficiaryRoleType) {
        this.secondBeneficiaryRoleType = secondBeneficiaryRoleType;
    }

    public BigDecimal getFirstAmount() {
        return firstAmount;
    }

    public void setFirstAmount(BigDecimal firstAmount) {
        this.firstAmount = firstAmount;
    }

    public BigDecimal getSecondAmount() {
        return secondAmount;
    }

    public void setSecondAmount(BigDecimal secondAmount) {
        this.secondAmount = secondAmount;
    }

    public boolean isUsePercentCharge() {
        return usePercentCharge;
    }

    public void setUsePercentCharge(boolean usePercentCharge) {
        this.usePercentCharge = usePercentCharge;
    }

    public double getPercentCharge() {
        return percentCharge;
    }

    public void setPercentCharge(double percentCharge) {
        this.percentCharge = percentCharge;
    }
}
