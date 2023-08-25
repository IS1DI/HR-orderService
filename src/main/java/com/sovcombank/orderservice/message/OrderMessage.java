package com.sovcombank.orderservice.message;

import com.sovcombank.orderservice.message.event.OrderEvent;
import com.sovcombank.orderservice.service.OrderEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class OrderMessage {
    private final OrderEventService orderService;
    @Bean
    public Consumer<OrderEvent.OrderChangedStatus> handleOrder() {
        return order -> {
            log.info("order received id = {} status = {}", order.id(),order.status());
            orderService.changeStatus(order.id(),order.status());
        };
    }
    @Bean
    public Sinks.Many<OrderEvent.OrderChangedStatus> orderChangedProducer(){
        return Sinks.many().replay().latest();
    }
    @Bean
    public Sinks.Many<OrderEvent.Order> orderCreatedProducer(){
        return Sinks.many().replay().latest();
    }
    @Bean
    public Sinks.Many<OrderEvent.OrderHrbp> orderHrbpProducer(){
        return Sinks.many().replay().latest();
    }

    @Bean
    public Supplier<Flux<OrderEvent.OrderChangedStatus>> orderChangedSupplier(){
        return () -> orderChangedProducer().asFlux();
    }
    @Bean
    public Supplier<Flux<OrderEvent.Order>> orderCreatedSupplier(){
        return () -> orderCreatedProducer().asFlux();
    }
    @Bean
    public Supplier<Flux<OrderEvent.OrderHrbp>> orderHrbpSupplier(){
        return () -> orderHrbpProducer().asFlux();
    }
}
