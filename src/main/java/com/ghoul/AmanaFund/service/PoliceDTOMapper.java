package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.DTO.PoliceDTO;
import com.ghoul.AmanaFund.entity.Police;
import org.springframework.stereotype.Service;

import java.util.function.Function;
@Service
public class PoliceDTOMapper implements Function <Police, PoliceDTO> {
    @Override
    public PoliceDTO apply(Police police) {
        return new PoliceDTO(
                police.getIdPolice(),
                police.isActive(),
                police.getStart(),
                police.getEnd(),
                police.getAmount(),
                police.getFrequency(),
                police.getRenewalDate(),
                police.getUser().getId()
        );
    }
}
