package com.ghoul.AmanaFund.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessErrorCodes {
    NO_CODE(0,HttpStatus.NOT_IMPLEMENTED,"No code"),
    INCORRECT_CURRENT_PASSWORD(300,HttpStatus.BAD_REQUEST,"Incorrect current password"),
    NEW_PASSWORD_DOES_NOT_MATCH(301,HttpStatus.BAD_REQUEST,"Password does not match"),
    ACCOUNT_LOCKED(302,HttpStatus.FORBIDDEN,"Account locked"),
    ACCOUNT_DISABLED(303,HttpStatus.FORBIDDEN,"Account disabled"),
    BAD_CREDENTIALS(304,HttpStatus.BAD_REQUEST,"Bad credentials"),
    ACTIVITY_LOG(304,HttpStatus.BAD_REQUEST,"Activity log"),
    ;
    private final int code;

    private final String description;

    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code,HttpStatus httpStatus, String description ) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
