package com.ghoul.AmanaFund.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentStatisticsDTO {
    private String period;
    private Double totalAmount;
}
