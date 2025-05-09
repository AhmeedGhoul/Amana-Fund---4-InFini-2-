package com.ghoul.AmanaFund.service;
import com.ghoul.AmanaFund.DTO.PoliceDTO;
import com.ghoul.AmanaFund.entity.FrequencyPolice;
import com.ghoul.AmanaFund.entity.Police;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IpoliceService {
    Police addPolice(PoliceDTO dto);

    List<PoliceDTO> retrievePolices();

    Police updatePolice(PoliceDTO police);

    PoliceDTO retrievePolice(Long idPolice);
    Police deactivatePolice(Long id);

    void removePolice(Long idPolice);

    public Page<PoliceDTO> getAllPaginated(Pageable pageable);

    public List<Police> searchPolice(Double amount);
}
