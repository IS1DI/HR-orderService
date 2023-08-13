package com.sovcombank.orderservice.service;

import com.sovcombank.orderservice.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class StreamService {

    @Autowired
    StreamBridge streamBridge;

}
