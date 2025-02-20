package com.ghoul.AmanaFund.service;
import com.ghoul.AmanaFund.repository.IpoliceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.ghoul.AmanaFund.entity.Police;
import com.ghoul.AmanaFund.service.IpoliceService;
import java.util.List;


@Service
@AllArgsConstructor

public class PoliceService implements IpoliceService{
    IpoliceRepository ipoliceRepository;
    @Override
    public Police addPolice(Police police) {
        return ipoliceRepository.save(police);
    }

    @Override
    public List<Police> retrievePolices() {
        return ipoliceRepository.findAll();
    }

    @Override
    public Police updatePolice(Police police) {
        return ipoliceRepository.save(police);
    }

    @Override
    public Police retrievePolice(Long idPolice) {
        return ipoliceRepository.findById(idPolice).orElse(null);
    }

    @Override
    public void removePolice(Long idPolice) {
        ipoliceRepository.deleteById(idPolice);
    }
}
