package com.ghoul.AmanaFund.service;


public class FraudDetectionException extends Exception {
    public FraudDetectionException(String message) {
        super(message);
    }

    public FraudDetectionException(String message, Throwable cause) {
        super(message, cause);
    }
}