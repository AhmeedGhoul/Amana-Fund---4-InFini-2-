package com.ghoul.AmanaFund.service;
import com.ghoul.AmanaFund.repository.IrequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.ghoul.AmanaFund.entity.Request;
import com.ghoul.AmanaFund.service.IrequestService;
import java.util.List;


@Service
@AllArgsConstructor

public class RequestService implements IrequestService{
    IrequestRepository irequestRepository;
    @Override
    public Request addRequest(Request request) {
        return irequestRepository.save(request);
    }

    @Override
    public List<Request> retrieveRequests() {
        return irequestRepository.findAll();
    }

    @Override
    public Request updateRequest(Request request) {
        return irequestRepository.save(request);
    }

    @Override
    public Request retrieveRequest(Integer idRequest) {
        return irequestRepository.findById(idRequest).orElse(null);
    }

    @Override
    public void removeRequest(Integer idRequest) {
        irequestRepository.deleteById(idRequest);
    }
}
