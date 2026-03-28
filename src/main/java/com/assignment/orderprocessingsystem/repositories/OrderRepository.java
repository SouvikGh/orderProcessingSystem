package com.assignment.orderprocessingsystem.repositories;

import com.assignment.orderprocessingsystem.entities.Order;
import com.assignment.orderprocessingsystem.utilities.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE Order o SET o.status = :newStatus, o.updatedAt = :now WHERE o.status = :currentStatus")
    int bulkUpdateStatus(@Param("currentStatus") OrderStatus currentStatus,
                         @Param("newStatus") OrderStatus newStatus,
                         @Param("now") LocalDateTime now);
}
