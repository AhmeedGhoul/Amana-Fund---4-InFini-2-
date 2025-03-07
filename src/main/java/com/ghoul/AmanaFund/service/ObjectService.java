package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.ObjectG;
import com.ghoul.AmanaFund.repository.IobjectRepository;
import com.ghoul.AmanaFund.repository.IpersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class ObjectService implements IobjectService{
    @Autowired
    private IobjectRepository iobjectRepository;
    @Override
    public ObjectG addGObjectG(ObjectG objectG) {
        return iobjectRepository.save(objectG);
    }

    @Override
    public List<ObjectG> retrieveObjectGs() {
        return iobjectRepository.findAll();
    }

    @Override
    public ObjectG updateObjectG(ObjectG objectG) {
        return iobjectRepository.save(objectG);
    }

    @Override
    public ObjectG retrieveObjectG(Long idObjectG) {
        return iobjectRepository.findById(idObjectG).orElse(null);
    }

    @Override
    public void removeObjectG(Long idObjectG) {
        iobjectRepository.deleteById(idObjectG);
    }

    @Override
    public Page<ObjectG> getAllPaginated(Pageable pageable) {
        return iobjectRepository.findAll(pageable);
    }
}
