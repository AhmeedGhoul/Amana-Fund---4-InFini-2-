package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.CreditPool;

import java.util.List;
import java.util.Map;

public interface ICreaditPoolService {
    CreditPool addCreditPool(CreditPool creditPool);
    List<CreditPool> retrieveCreditPools();
    CreditPool updateCreditPool(CreditPool creditPool);
    CreditPool retrieveCreditPool(int idPool);
    void removeCreditPool(int idPool);
    public Map<Integer, Double> calculateInterestRatesForPool(CreditPool creditPool) ;

}
