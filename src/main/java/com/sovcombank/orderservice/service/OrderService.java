package com.sovcombank.orderservice.service;

import com.sovcombank.orderservice.dto.OrderDTO;
import com.sovcombank.orderservice.entity.Order;
import com.sovcombank.orderservice.entity.Status;
import com.sovcombank.orderservice.mapper.OrderMapper;
import com.sovcombank.orderservice.message.event.OrderEvent;
import com.sovcombank.orderservice.repository.OrderRepository;
import com.sovcombank.orderservice.security.Roles;
import com.sovcombank.orderservice.utils.MessageMethod;
import com.sovcombank.orderservice.utils.OrderUtils;
import com.sovcombank.orderservice.utils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final MessageService messageService;
    private final OrderRepository orderRepository;
    private final Sinks.Many<OrderEvent.Order> orderCreatedProducer;
    private final Sinks.Many<OrderEvent.OrderChangedStatus> orderChangedProducer;

    public Order getOrderById(String orderId, Locale locale) {
            Optional<Order> order = orderRepository.findById(orderId);
            order.ifPresentOrElse(
                    o -> {
                        /*if (UserUtils.isAllowed(auth, List.of(Roles.CUSTOMER)) && !o.getOwnerId().equals(userId)
                                || UserUtils.isAllowed(auth, List.of(Roles.HRBP))&&!o.getHrbps().contains(userId)) {
                            throw new AccessDeniedException(messageService.accessErrorMessage(locale));
                        }*/
                    }, () -> {
                        throw new EntityNotFoundException(messageService.getMessage(MessageMethod.ORDER_SEARCH_ERROR,new Object[]{orderId},locale));
                    }
            );
            return order.get();
    }

    public String create(OrderDTO orderDTO, String userId, Locale locale) { //TODO kafka and redis and circuit breaker
        var order = OrderMapper.fromOrderDTO(orderDTO);
        order.setOwnerId(userId);
        order.setCreatedAt(Instant.now());
        if(order.getMaxHrbp() == 0){
            order.setMaxHrbp(2);
        }
        order.setStatus(Status.CREATED);
        orderRepository.save(order);
        orderCreatedProducer.tryEmitNext(OrderMapper.toOrder(order)); //TODO
        log.info("order {} created by user {}", order.getId(), userId);
        return messageService.getMessage(MessageMethod.ORDER_CREATE, new Object[]{order.getId()}, locale);
    }

    public String changeStatus(String orderId, String userId, Locale locale) { //TODO and roles kafka and redis and circuit breaker
        Optional<Order> order = orderRepository.findById(orderId);
        Status next;
        if (order.isPresent() && !order.get().getStatus().equals(next = Status.getNext(order.get().getStatus()))) {
            order.get().setStatus(next);
            log.info("status of order {} changed to {} by user {}", orderId, next, userId);
            orderChangedProducer.tryEmitNext(OrderMapper.toOrderChangedStatus(order.get()));
            return messageService.getMessage(MessageMethod.ORDER_UPDATE, new Object[]{orderId}, locale);
        }
        throw new EntityNotFoundException(messageService.getMessage(MessageMethod.ORDER_SEARCH_ERROR,new Object[]{orderId},locale));
    }

    public String closeOrder(String orderId, String userId, Locale locale) { //TODO kafka and redis and circuit breaker
        orderRepository
                .findById(orderId)
                .ifPresentOrElse((o) -> {
                            if (OrderUtils.access(userId, o.getHrbps())) {
                                log.info("order {} closed by user {}", orderId, userId);
                                o.setClosedAt(Instant.now());
                                o.setStatus(Status.CLOSED);
                                orderChangedProducer.tryEmitNext(OrderMapper.toOrderChangedStatus(o));
                            } else {
                                throw new AccessDeniedException(messageService.getMessage(MessageMethod.ORDER_ACCESS_ERROR, new Object[]{orderId}, locale));
                            }
                        },
                        () -> {
                            log.debug("order {} not found", orderId);
                            throw new EntityNotFoundException(messageService.getMessage(MessageMethod.ORDER_SEARCH_ERROR,new Object[]{orderId},locale));

                        });
        return messageService.getMessage(MessageMethod.ORDER_DELETE, new Object[]{orderId}, locale);
    }


    public Page<Order> getPage(Principal principal, Pageable pageable, Locale locale) {

        if (UserUtils.isAllowed(principal, List.of(Roles.CUSTOMER)))
            return orderRepository.findAllByOwnerIdOrderByCreatedAtDesc(pageable, UserUtils.getUserId(principal));
        else if (UserUtils.isAllowed(principal, List.of(Roles.HRBP)))
            return orderRepository.findAllByClosedAtIsNotNullAndHrbpsContains(pageable, UserUtils.getUserId(principal));
        else
            throw new AccessDeniedException(messageService.getMessage(MessageMethod.ORDER_ACCESS_ERROR, null, locale));
    }

}
