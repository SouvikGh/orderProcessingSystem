package com.assignment.orderprocessingsystem.jobs;

import com.assignment.orderprocessingsystem.repositories.OrderRepository;
import com.assignment.orderprocessingsystem.utilities.OrderStatus;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class OrderStatusScheduler {

    @Autowired
    private OrderRepository orderRepository;

    @Scheduled(cron = "${spring.background.jobs.processOrder}")
    @Transactional
    public void processOrderStatus() {
        log.info("Scheduler triggered at {} — promoting PENDING orders to PROCESSING", LocalDateTime.now());
        int updatedCount = orderRepository.bulkUpdateStatus(
                OrderStatus.PENDING,
                OrderStatus.PROCESSING,
                LocalDateTime.now()
        );
        log.info("Scheduler completed — {} order(s) updated from PENDING to PROCESSING", updatedCount);
    }
}