package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.DTO.ObjectGDTO;
import com.ghoul.AmanaFund.entity.ObjectG;
import com.ghoul.AmanaFund.entity.Police;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DTOObjectMapper {
    @Autowired
    private PoliceService policeService;
    public ObjectG apply(ObjectGDTO dto) {
        Police police = policeService.getPoliceById(dto.getPoliceId());

        return ObjectG.builder()
                .idGarantie(dto.getIdGarantie())
                .Active(dto.isActive())
                .Documents(dto.getDocuments())
                .ownershipCertifNumber(dto.getOwnershipCertifNumber())
                .estimatedValue(dto.getEstimatedValue())
                .type(dto.getType())
                .police(police)
                .build();
    }
}
