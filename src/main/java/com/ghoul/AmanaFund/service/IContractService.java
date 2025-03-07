package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Contract;
import com.ghoul.AmanaFund.entity.CreditPool;
import com.ghoul.AmanaFund.entity.Payment;

import java.util.List;
import java.util.Map;

public interface IContractService {
    Contract AddContract(Contract contract);
    List<Contract> retriveContract();
    Contract updateContract (Contract contract);
    Contract retriveContractById(int id_contract);
    void removeContract(int id_contract);
    public List<List<Integer>> getRecoveryOptionsForContract(Contract contract, CreditPool creditPool, List<Contract> allContracts) ;
}
