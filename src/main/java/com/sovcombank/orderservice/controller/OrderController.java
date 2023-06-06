package com.sovcombank.orderservice.controller;

import com.sovcombank.orderservice.entity.Order;
import com.sovcombank.orderservice.service.OrderService;
import com.sovcombank.orderservice.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Locale;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{orderId}")
    ResponseEntity<Order> getOrderById(@PathVariable String orderId,
                                       Principal principal,
                                       @RequestHeader(value = "Accept-Language", required = false) Locale locale){
        return ok(orderService.getOrderById(orderId,principal,locale));
    }


    /**
     * only for CUSTOMER role
     * save order log date created &&
     * send message to Message Broker with status CREATED &&
     * save to Redis cache
     *
     * @param order - order representation
     */
    @PostMapping("/")
    @PreAuthorize("hasRole('CUSTOMER')")
    //TODO roles
    ResponseEntity<String> createOrder(@RequestBody Order order, Principal principal,
                                       @RequestHeader(value = "Accept-Language", required = false) Locale locale){
        return ok(orderService.create(order, UserUtils.getUserId(principal),locale));
    }

    /**
     * change status of orderById and log
     * set up roles
     *
     */
    @PutMapping("/{orderId}")
    //TODO roles
    ResponseEntity<String> changeStatus(@PathVariable String orderId, Principal principal,
                                        @RequestHeader(value = "Accept-Language", required = false) Locale locale){
        return ok(orderService.changeStatus(orderId, UserUtils.getUserId(principal), locale));
    }

    /**
     * only for ??? hasRole("HRBP") ??? //TODO
     * deleting resume and log
     *
     */
    @DeleteMapping("/{orderId}")
    ResponseEntity<String> closeOrder(@PathVariable String orderId, Principal principal,
                                      @RequestHeader(value = "Accept-Language", required = false) Locale locale){
        return ok(orderService.closeOrder(orderId,UserUtils.getUserId(principal),locale));
    }

    /**
     * all orders by client service
     *
     */
    @GetMapping("/all")
    //TODO roles and filters
    Page<Order> getAllOrders(Principal principal,
                             Pageable pageable,
                             Locale locale){
        return orderService.getPage(principal,pageable, locale);
    }
}
