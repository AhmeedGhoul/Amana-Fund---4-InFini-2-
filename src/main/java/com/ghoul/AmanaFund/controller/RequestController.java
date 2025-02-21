package com.ghoul.AmanaFund.controller;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.ghoul.AmanaFund.entity.*;
import com.ghoul.AmanaFund.service.*;

import java.awt.*;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("Request")
@Tag(name = "Request")

public class RequestController {
    IrequestService irequestService;
    @PostMapping("add_request")
    public Request addRequest(@RequestBody Request request)
    {
        return irequestService.addRequest(request);
    }
    @GetMapping("getall_request")
    public List<Request> GetAllRequest()
    {
        return irequestService.retrieveRequests();
    }
    @PutMapping("update_request")
    public Request updateRequest(@RequestBody Request request)
    {
        return irequestService.updateRequest(request);
    }
}
