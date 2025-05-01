package com.ghoul.AmanaFund.DTO;

import com.ghoul.AmanaFund.entity.FrequencyPolice;
import lombok.*;

import java.util.Date;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PoliceDTO {
    private Long idPolice;
    private boolean active;
    private Date start;
    private Date end;
    private Double amount;
    private FrequencyPolice frequency;
    private Date renewalDate;
    private Integer userId;
}
