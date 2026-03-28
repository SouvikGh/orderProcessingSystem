package com.assignment.orderprocessingsystem.services;

import com.assignment.orderprocessingsystem.dataObjects.OrderDTO;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(OrderDTO order);

    Boolean cancelOrder(Long id);

    OrderDTO getOrderById(Long id);

    List<OrderDTO> getAllOrders(String status);

    OrderDTO updateOrderStatus(Long id, OrderDTO request);
}
