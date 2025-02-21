package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Request;

import java.util.List;

public interface IrequestService {
    Request addRequest(Request request);
    List<Request> retrieveRequests();
    Request updateRequest(Request request);
    Request retrieveRequest(Integer idRequest);
    void removeRequest(Integer idRequest);
}
