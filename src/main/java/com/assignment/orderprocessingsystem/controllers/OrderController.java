package com.assignment.orderprocessingsystem.controllers;

import com.assignment.orderprocessingsystem.dataObjects.OrderDTO;
import com.assignment.orderprocessingsystem.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/orders")
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO order) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(order));
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.cancelOrder(id).equals(Boolean.TRUE) ? "Order is Successfully Cancelled!!" : "");
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id,
                                                      @RequestBody OrderDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.updateOrderStatus(id, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders(@RequestParam(required = false) String status) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrders(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderById(id));
    }
}
