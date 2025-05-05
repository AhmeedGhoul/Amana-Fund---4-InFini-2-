package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.DTO.ObjectGDTO;
import com.ghoul.AmanaFund.entity.ObjectG;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ObjectGDTOMapper implements Function <ObjectG, ObjectGDTO> {
    @Override
    public ObjectGDTO apply(ObjectG objectG) {
        return new ObjectGDTO(
                objectG.getIdGarantie(),
                objectG.isActive(),
                objectG.getDocuments(),
                objectG.getOwnershipCertifNumber(),
                objectG.getEstimatedValue(),
                objectG.getType(),
                objectG.getPolice().getIdPolice()
        );
    }
}
