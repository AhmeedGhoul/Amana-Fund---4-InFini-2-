package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.DTO.ObjectGDTO;
import com.ghoul.AmanaFund.entity.ObjectG;
import com.ghoul.AmanaFund.repository.IobjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ObjectService implements IobjectService{
    private final ObjectGDTOMapper objectGDTOMapper;
    @Autowired
    private IobjectRepository iobjectRepository;
    @Override
    public ObjectG addGObjectG(ObjectG objectG) {
        return iobjectRepository.save(objectG);
    }

    @Override
    public List<ObjectGDTO> retrieveObjectGs() {
        return iobjectRepository
                .findAll()
                .stream()
                .map(objectGDTOMapper)
                .collect(Collectors.toList())
                ;
    }

    @Override
    public ObjectG updateObjectG(ObjectG objectG) {
        return iobjectRepository.save(objectG);
    }

    @Override
    public ObjectGDTO retrieveObjectG(Long idObjectG) {
        return iobjectRepository
                .findById(idObjectG)
                .map(objectGDTOMapper)
                .orElse(null);
    }

    @Override
    public void removeObjectG(Long idObjectG) {
        iobjectRepository.deleteById(idObjectG);
    }

    @Override
    public Page<ObjectGDTO> getAllPaginated(Pageable pageable) {
        return iobjectRepository
                .findAll(pageable)
                .map(objectGDTOMapper)
                ;
    }
}
