package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.DTO.ObjectGDTO;
import com.ghoul.AmanaFund.entity.ObjectG;
import com.ghoul.AmanaFund.entity.Person;
import com.ghoul.AmanaFund.entity.Police;
import com.ghoul.AmanaFund.repository.IobjectRepository;
import com.ghoul.AmanaFund.repository.IpoliceRepository;
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
    @Autowired
    private IpoliceRepository policeRepository;
    private final ObjectGDTOMapper objectGDTOMapper;
    private final DTOObjectMapper dtoObjectMapper;

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
    public ObjectG deactivatePerson(Long id) {
        ObjectG person = iobjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Object not found with id " + id));
        if (person.isActive())
            person.setActive(false);
        else
            person.setActive(true);
        return iobjectRepository.save(person);
    }
    public ObjectG addDTOObject(ObjectGDTO objectGDTO) {
        if (objectGDTO == null) {
            throw new RuntimeException("ObjectGDTO should not be null");
        }

        ObjectG objectG = dtoObjectMapper.apply(objectGDTO);
        return iobjectRepository.save(objectG);
    }
    @Override
    public ObjectG updateObjectGFromDTO(ObjectGDTO dto) {
        ObjectG objectG = iobjectRepository.findById(dto.getIdGarantie())
                .orElseThrow(() -> new RuntimeException("Object not found"));

        objectG.setActive(dto.isActive());
        objectG.setDocuments(dto.getDocuments());
        objectG.setOwnershipCertifNumber(dto.getOwnershipCertifNumber());
        objectG.setEstimatedValue(dto.getEstimatedValue());
        objectG.setType(dto.getType());

        if (dto.getPoliceId() != null) {
            Police police = policeRepository.findById(dto.getPoliceId())
                    .orElseThrow(() -> new RuntimeException("Police not found"));
            objectG.setPolice(police);
        }

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
