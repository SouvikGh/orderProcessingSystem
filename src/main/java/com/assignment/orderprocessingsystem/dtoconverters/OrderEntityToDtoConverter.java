package com.assignment.orderprocessingsystem.dtoconverters;


import com.assignment.orderprocessingsystem.dataObjects.ItemDTO;
import com.assignment.orderprocessingsystem.dataObjects.OrderDTO;
import com.assignment.orderprocessingsystem.entities.Order;
import com.assignment.orderprocessingsystem.entities.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderEntityToDtoConverter {


    public OrderDTO orderDtoConvert(Order order) {
        List<OrderItem> allOrderItems = order.getOrderItems();
        List<ItemDTO> itemList = allOrderItems.stream().map(
                orderItem -> ItemDTO.builder().itemName(orderItem.getItem().getItemName())
                        .id(orderItem.getItem().getId())
                        .quantity(orderItem.getQuantity())
                        .unitPriceAtOrderTime(orderItem.getUnitPriceAtOrderTime())
                        .totalItemAmount(orderItem.getLineTotal())
                        .build()).toList();


        return OrderDTO.builder().
                orderReferenceId(order.getOrderReferenceId()).
                description(order.getDescription()).
                totalAmount(order.getTotalAmount()).
                createdAt(order.getCreatedAt().toString()).
                items(itemList).
                status(order.getStatus().toString()).
                build();
    }
}
