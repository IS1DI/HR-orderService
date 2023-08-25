package com.sovcombank.orderservice.controller;

import com.sovcombank.orderservice.dto.OrderDTO;
import com.sovcombank.orderservice.entity.Order;
import com.sovcombank.orderservice.service.OrderService;
import com.sovcombank.orderservice.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Locale;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('HRBP')")
    @PostAuthorize("(hasRole('CUSTOMER') and returnObject.body.ownerId.equals(#token.tokenAttributes.get('sub')))" +
            "or (hasRole('HRBP') and returnObject.body.hrbps.contains(#token.tokenAttributes.get('sub')))")
    ResponseEntity<Order> getOrderById(@PathVariable String orderId,
            /*@RequestHeader(value = "Accept-Language", required = false)*/
                                       JwtAuthenticationToken token,
                                       Locale locale) {
        Order order = orderService.getOrderById(orderId, locale);
        return ok(order);
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
    ResponseEntity<String> createOrder(@RequestBody OrderDTO order,
                                       Principal principal,
            /*@RequestHeader(value = "Accept-Language", required = false)*/ Locale locale) {
        return ok(orderService.create(order, UserUtils.getUserId(principal), locale));
    }

    /**
     * change status of orderById and log
     * set up roles
     */
    @PutMapping("/{orderId}")
    //TODO roles
    ResponseEntity<String> changeStatus(@PathVariable String orderId, Principal principal,
            /*@RequestHeader(value = "Accept-Language", required = false)*/ Locale locale) {
        return ok(orderService.changeStatus(orderId, UserUtils.getUserId(principal), locale));
    }

    /**
     * only for ??? hasRole("HRBP") ??? //TODO
     * deleting resume and log
     */
    @DeleteMapping("/{orderId}")
    ResponseEntity<String> closeOrder(@PathVariable String orderId, Principal principal,
            /*@RequestHeader(value = "Accept-Language", required = false)*/ Locale locale) {
        return ok(orderService.closeOrder(orderId, UserUtils.getUserId(principal), locale));
    }

    @GetMapping("/roles")
    ResponseEntity<?> getRoles(Authentication p) {
        return ok(p.getAuthorities());
    }

    /**
     * all orders by client service
     */
    @GetMapping("/all")
    //TODO roles and filters
    Page<Order> getAllOrders(Principal principal,
                             Pageable pageable,
                             Locale locale) {
        return orderService.getPage(principal, pageable, locale);
    }
}
