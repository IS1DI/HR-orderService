package com.sovcombank.orderservice.dto;

import lombok.Data;

@Data
public class OrderDTO {
    private String title;
    private String body;
    private int maxHrbp;
}
