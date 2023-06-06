package com.sovcombank.orderservice.utils;

public enum MessageMethod {


    CREATE("create.message"),
    UPDATE("update.message"),
    DELETE("delete.message"),
    SEARCH_ERROR("search.error.message"),
    ACCESS_ERROR("access.error.message");

    private final String val;

    MessageMethod(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
