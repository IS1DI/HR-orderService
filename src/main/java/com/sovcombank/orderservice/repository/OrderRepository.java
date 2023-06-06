package com.sovcombank.orderservice.repository;

import com.sovcombank.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,String> {
    Page<Order> findAllByClosedAtIsNotNullAndHrbpsContains(Pageable pageable,String hrbpId);
    Page<Order> findAllByOwnerIdOrderByCreatedAtDesc(Pageable pageable, String ownerId);
}
