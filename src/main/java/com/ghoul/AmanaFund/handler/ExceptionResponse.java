package com.ghoul.AmanaFund.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {
    private String businessErrorDescription;
    private Integer businessErrorCode;
    private String error;
    private Set<String> validationErrors;
    private Map<String, String> errors;



}
