package com.sovcombank.orderservice.message.event;



import com.sovcombank.orderservice.entity.Status;

import java.io.Serializable;
import java.util.Set;

public sealed interface OrderEvent extends Serializable {
    enum EventStatus implements OrderEvent{
        ORDER_CREATED,
        STATUS_CHANGED,
        HRBP_ADDED,
        HRBP_REMOVED
    }
    record Order(String id, String ownerId, Set<String> hrbps, Status status, EventStatus eventStatus) implements OrderEvent{}

    record OrderChangedStatus(String id, Status status) implements OrderEvent{}
    record OrderHrbp(String id, String hrbp, EventStatus eventStatus) implements OrderEvent{}

}
