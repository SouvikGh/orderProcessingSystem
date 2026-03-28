package com.assignment.orderprocessingsystem.services.implementions;

import com.assignment.orderprocessingsystem.dataObjects.ItemDTO;
import com.assignment.orderprocessingsystem.dataObjects.OrderDTO;
import com.assignment.orderprocessingsystem.dtoconverters.OrderEntityToDtoConverter;
import com.assignment.orderprocessingsystem.entities.Item;
import com.assignment.orderprocessingsystem.entities.Order;
import com.assignment.orderprocessingsystem.entities.OrderItem;
import com.assignment.orderprocessingsystem.exceptions.NotFoundException;
import com.assignment.orderprocessingsystem.exceptions.OpsNotAllowed;
import com.assignment.orderprocessingsystem.repositories.ItemRepository;
import com.assignment.orderprocessingsystem.repositories.OrderRepository;
import com.assignment.orderprocessingsystem.services.OrderService;
import com.assignment.orderprocessingsystem.utilities.OrderStatus;
import com.assignment.orderprocessingsystem.utilities.Utils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(OrderStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(OrderStatus.PENDING, EnumSet.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.PROCESSING, EnumSet.of(OrderStatus.SHIPPED));
        ALLOWED_TRANSITIONS.put(OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED));
        ALLOWED_TRANSITIONS.put(OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class));
        ALLOWED_TRANSITIONS.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
    }

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private Utils utils;

    @Autowired
    private OrderEntityToDtoConverter orderEntityToDtoConverter;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO request) {
        Order order = new Order();
        order.setOrderReferenceId(utils.generateReferenceNumber());
        order.setDescription(request.getDescription());

        BigDecimal total = BigDecimal.ZERO;
        for (ItemDTO line : request.getItems()) {
            Item item = itemRepository.findById(line.getId())
                    .orElseThrow(() -> new NotFoundException("Item not found for: " + line.getId()));
            BigDecimal unitPrice = item.getItemUnitPrice();
            OrderItem detail = new OrderItem();
            detail.setOrder(order);
            detail.setItem(item);
            detail.setQuantity(line.getQuantity());
            detail.setUnitPriceAtOrderTime(unitPrice);
            order.getOrderItems().add(detail);
            total = total.add(detail.getLineTotal());
        }
        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        return orderEntityToDtoConverter.orderDtoConvert(saved);
    }

    @Override
    public Boolean cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));
        if (order.getStatus().equals(OrderStatus.PENDING)) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            return true;
        } else {
            throw new OpsNotAllowed("Order cannot be cancelled");
        }
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));
        return orderEntityToDtoConverter.orderDtoConvert(order);
    }

    @Override
    public OrderDTO updateOrderStatus(Long id, OrderDTO request) {

        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Order not found: " + id));

            OrderStatus newStatus = OrderStatus.valueOf(request.getStatus().toUpperCase());

            OrderStatus currentStatus = order.getStatus();
            if (!ALLOWED_TRANSITIONS.get(currentStatus).contains(newStatus)) {
                throw new OpsNotAllowed(
                        "Cannot transition order from " + currentStatus + " to " + newStatus
                );
            }
            order.setStatus(newStatus);
            return orderEntityToDtoConverter.orderDtoConvert(orderRepository.save(order));
        } catch (IllegalArgumentException e) {
            throw new OpsNotAllowed("Invalid Order Request");
        }
    }

    @Override
    public List<OrderDTO> getAllOrders(String status) {
        List<Order> orders;
        if (status != null && !status.isBlank()) {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            orders = orderRepository.findByStatus(orderStatus);
        } else {
            orders = orderRepository.findAll();
        }
        return orders.stream()
                .map(orderEntityToDtoConverter::orderDtoConvert)
                .toList();
    }
}
