package com.ghoul.AmanaFund.service;
import com.ghoul.AmanaFund.entity.FrequencyPolice;
import com.ghoul.AmanaFund.entity.Police;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IpoliceService {
    Police addPolice(Police police);

    List<Police> retrievePolices();

    Police updatePolice(Police police);

    Police retrievePolice(Long idPolice);

    void removePolice(Long idPolice);

    public Page<Police> getAllPaginated(Pageable pageable);

    public List<Police> searchPolice(Date start, Double amount, Long id);
}
