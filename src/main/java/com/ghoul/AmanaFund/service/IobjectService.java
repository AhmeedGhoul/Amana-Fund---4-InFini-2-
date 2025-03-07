package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.ObjectG;
import com.ghoul.AmanaFund.entity.Police;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IobjectService {
    ObjectG addGObjectG(ObjectG objectG);
    List<ObjectG> retrieveObjectGs();
    ObjectG updateObjectG(ObjectG objectG);
    ObjectG retrieveObjectG(Long idObjectG);
    void removeObjectG(Long idObjectG);
    public Page<ObjectG> getAllPaginated(Pageable pageable);
}
