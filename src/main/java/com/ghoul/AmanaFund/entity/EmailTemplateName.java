package com.ghoul.AmanaFund.entity;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account"),
    NEWS_CASES("News_Cases");
    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}
