package com.tms.model.order;

import com.tms.model.Model;

import java.math.BigDecimal;

public class Order extends Model {

    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal totalTax = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;



}
