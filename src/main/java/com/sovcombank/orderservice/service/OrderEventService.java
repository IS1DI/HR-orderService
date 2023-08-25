package com.sovcombank.orderservice.service;

import com.sovcombank.orderservice.entity.Order;
import com.sovcombank.orderservice.entity.Status;
import com.sovcombank.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventService {
    private final OrderRepository orderRepository;


    public void changeStatus(String id, Status status) {
        Optional<Order> opt = orderRepository.findById(id);
        if(status.equals(Status.ALL_VACANCIES_CLOSED)&&opt.isPresent()){
            opt.get().setStatus(status);
        }
    }
}
