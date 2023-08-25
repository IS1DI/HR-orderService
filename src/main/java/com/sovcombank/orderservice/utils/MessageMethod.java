package com.sovcombank.orderservice.utils;

public enum MessageMethod {


    ORDER_CREATE("order.create.message"),
    ORDER_UPDATE("order.update.message"),
    ORDER_DELETE("order.delete.message"),
    ORDER_SEARCH_ERROR("order.search.error.message"),
    ORDER_ACCESS_ERROR("order.access.error.message"),
    ACCESS_ERROR("access.error.message");

    private final String val;

    MessageMethod(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
