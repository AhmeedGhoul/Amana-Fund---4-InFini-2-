package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.DTO.ObjectGDTO;
import com.ghoul.AmanaFund.entity.ObjectG;
import com.ghoul.AmanaFund.entity.Police;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IobjectService {
    ObjectG addGObjectG(ObjectG objectG);
    List<ObjectGDTO> retrieveObjectGs();
    ObjectG updateObjectG(ObjectG objectG);
    ObjectGDTO retrieveObjectG(Long idObjectG);
    void removeObjectG(Long idObjectG);
    public Page<ObjectGDTO> getAllPaginated(Pageable pageable);
}
