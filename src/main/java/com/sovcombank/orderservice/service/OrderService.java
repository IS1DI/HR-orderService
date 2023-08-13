package com.sovcombank.orderservice.service;

import com.sovcombank.orderservice.client.UserServiceClient;
import com.sovcombank.orderservice.entity.Order;
import com.sovcombank.orderservice.entity.Status;
import com.sovcombank.orderservice.repository.OrderRepository;
import com.sovcombank.orderservice.security.Roles;
import com.sovcombank.orderservice.utils.MessageMethod;
import com.sovcombank.orderservice.utils.OrderUtils;
import com.sovcombank.orderservice.utils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
@Slf4j
@Service
public class OrderService {
    @Autowired
    MessageService messageService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UserServiceClient userServiceClient;
    @Autowired
    StreamBridge streamBridge;

    public Order getOrderById(String orderId, Principal principal, Locale locale) {
        String userId = UserUtils.getUserId(principal);
        if (UserUtils.isAllowed(principal, List.of(Roles.CUSTOMER, Roles.HRBP))) {
            Optional<Order> order = orderRepository.findById(orderId);
            order.ifPresentOrElse(
                    o -> {
                        if (UserUtils.isAllowed(principal, List.of(Roles.CUSTOMER)) && !o.getOwnerId().equals(userId)
                                || UserUtils.isAllowed(principal, List.of(Roles.HRBP))&&!o.getHrbps().contains(userId)) {
                            throw new AccessDeniedException(messageService.accessErrorMessage(locale));
                        }
                    }, () -> {
                        throw new EntityNotFoundException(messageService.orderMessage(MessageMethod.SEARCH_ERROR,new Object[]{orderId},locale));
                    }
            );
            return order.get();
        } else
            throw new AccessDeniedException(messageService.accessErrorMessage(locale));
    }

    public String create(Order order,int countHRBPs, boolean auto, String userId, Locale locale) { //TODO kafka and redis and circuit breaker
        order.setOwnerId(userId);
        order.setCreatedAt(Instant.now());
        if(auto)
            order.setHrbps(userServiceClient.getOptimalHRBPs(countHRBPs));
        orderRepository.save(order);
        streamBridge.send("orders",order); //TODO
        log.info("order {} created by user {}", order.getId(), userId);
        return messageService.orderMessage(MessageMethod.CREATE, new Object[]{order.getId()}, locale);
    }

    public String changeStatus(String orderId, String userId, Locale locale) { //TODO and roles kafka and redis and circuit breaker
        Optional<Order> order = orderRepository.findById(orderId);
        Status next;
        if (order.isPresent() && !order.get().getStatus().equals(next = Status.getNext(order.get().getStatus()))) {
            order.get().setStatus(next);
            log.info("status of order {} changed to {} by user {}", orderId, next, userId);
            return messageService.orderMessage(MessageMethod.UPDATE, new Object[]{orderId}, locale);
        }
        throw new EntityNotFoundException(messageService.orderMessage(MessageMethod.SEARCH_ERROR,new Object[]{orderId},locale));
    }

    public String closeOrder(String orderId, String userId, Locale locale) { //TODO kafka and redis and circuit breaker
        orderRepository
                .findById(orderId)
                .ifPresentOrElse((o) -> {
                            if (OrderUtils.access(userId, o.getHrbps())) {
                                log.info("order {} closed by user {}", orderId, userId);
                                o.setClosedAt(Instant.now());
                                o.setStatus(Status.CLOSED);
                            } else {
                                throw new AccessDeniedException(messageService.orderMessage(MessageMethod.ACCESS_ERROR, new Object[]{orderId}, locale));
                            }
                        },
                        () -> {
                            log.debug("order {} not found", orderId);
                            throw new EntityNotFoundException(messageService.orderMessage(MessageMethod.SEARCH_ERROR,new Object[]{orderId},locale));

                        });
        return messageService.orderMessage(MessageMethod.DELETE, new Object[]{orderId}, locale);
    }


    public Page<Order> getPage(Principal principal, Pageable pageable, Locale locale) {

        if (UserUtils.isAllowed(principal, List.of(Roles.CUSTOMER)))
            return orderRepository.findAllByOwnerIdOrderByCreatedAtDesc(pageable, UserUtils.getUserId(principal));
        else if (UserUtils.isAllowed(principal, List.of(Roles.HRBP)))
            return orderRepository.findAllByClosedAtIsNotNullAndHrbpsContains(pageable, UserUtils.getUserId(principal));
        else
            throw new AccessDeniedException(messageService.orderMessage(MessageMethod.ACCESS_ERROR, null, locale));
    }


}
