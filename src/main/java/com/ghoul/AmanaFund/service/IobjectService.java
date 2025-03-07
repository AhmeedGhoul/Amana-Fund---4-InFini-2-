package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.ObjectG;

import java.util.List;

public interface IobjectService {
    ObjectG addGObjectG(ObjectG objectG);
    List<ObjectG> retrieveObjectGs();
    ObjectG updateObjectG(ObjectG objectG);
    ObjectG retrieveObjectG(Long idObjectG);
    void removeObjectG(Long idObjectG);
}
