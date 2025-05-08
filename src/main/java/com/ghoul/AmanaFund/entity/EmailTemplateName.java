package com.ghoul.AmanaFund.entity;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account"),
    RESET_PASSWORD("reset_password"),
    NEWS_CASES("news_cases"),
    PAYMENT_NOTIFICATION("payment_notification");  // Added new template

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}