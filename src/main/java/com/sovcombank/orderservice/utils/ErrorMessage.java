package com.sovcombank.orderservice.utils;

import lombok.Data;

import java.time.Instant;

@Data
public class ErrorMessage {
    private String message;
    private Instant timestamp;

    public ErrorMessage(String message) {
        this.message = message;
        timestamp = Instant.now();
    }
}
