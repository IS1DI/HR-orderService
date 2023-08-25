package com.sovcombank.orderservice.mapper;

import com.sovcombank.orderservice.dto.OrderDTO;
import com.sovcombank.orderservice.entity.Order;
import com.sovcombank.orderservice.message.event.OrderEvent;

public interface OrderMapper {

    static Order fromOrderDTO(OrderDTO orderDTO){
        var order = new Order();
        order.setTitle(orderDTO.getTitle());
        order.setBody(orderDTO.getBody());
        order.setMaxHrbp(orderDTO.getMaxHrbp());
        return order;
    }
    static OrderEvent.Order toOrder(Order order){
        return new OrderEvent.Order(
                order.getId(),
                order.getOwnerId(),
                order.getHrbps(),
                order.getStatus(),
                OrderEvent.EventStatus.ORDER_CREATED
                );

    }
    static OrderEvent.OrderChangedStatus toOrderChangedStatus(Order order){
        return new OrderEvent.OrderChangedStatus(
                order.getId(),
                order.getStatus()
        );
    }
    static OrderEvent.OrderHrbp toOrderHRBP(Order order,String hrbp){
        OrderEvent.EventStatus status;
        if(order.getHrbps().contains(hrbp)){
            status = OrderEvent.EventStatus.HRBP_ADDED;
        }else{
            status = OrderEvent.EventStatus.HRBP_REMOVED;
        }
        return new OrderEvent.OrderHrbp(
                order.getId(),
                hrbp,
                status
        );
    }
}
