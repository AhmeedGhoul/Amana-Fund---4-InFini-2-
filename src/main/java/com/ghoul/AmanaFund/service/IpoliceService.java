package com.ghoul.AmanaFund.service;
import com.ghoul.AmanaFund.entity.Police;
import java.util.List;

public interface IpoliceService {
    Police addPolice(Police police);
    List<Police> retrievePolices();
    Police updatePolice(Police police);
    Police retrievePolice(Long idPolice);
    void removePolice(Long idPolice);

}
