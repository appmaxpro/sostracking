package com.tms.model;

import com.tms.TmsStore;

import java.math.BigDecimal;
import java.util.Collection;

public interface PricingRuleContainer {

    BigDecimal getBaseRate();
    BigDecimal getRatePer100m();
    BigDecimal getRatePerMinute();
    BigDecimal getRatePerMinuteWait();
    BigDecimal getPerPayPercent();
    int getMinDistance();
    int getMaxDistance();
    Collection<TransRateRule> getRateRuleList(TmsStore store);
    Collection<TransTimeRateRule> getTimeRateRuleList(TmsStore store);
    Collection<TransTrafficRateRule> getTrafficRateRuleList(TmsStore store);

}
