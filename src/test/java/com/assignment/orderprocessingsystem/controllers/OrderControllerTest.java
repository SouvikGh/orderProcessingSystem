package com.assignment.orderprocessingsystem.controllers;

import com.assignment.orderprocessingsystem.dataObjects.ItemDTO;
import com.assignment.orderprocessingsystem.dataObjects.OrderDTO;
import com.assignment.orderprocessingsystem.exceptions.GlobalExceptionHandler;
import com.assignment.orderprocessingsystem.exceptions.NotFoundException;
import com.assignment.orderprocessingsystem.exceptions.OpsNotAllowed;
import com.assignment.orderprocessingsystem.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("POST /api/orders returns 201 and created order body")
    void createOrder_returnsCreatedAndBody() throws Exception {
        OrderDTO response = OrderDTO.builder()
                .orderReferenceId("REF-100")
                .description("Test order")
                .status("PENDING")
                .totalAmount(new BigDecimal("25.50"))
                .build();
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(response);

        OrderDTO request = OrderDTO.builder()
                .description("Test order")
                .items(List.of(ItemDTO.builder().id(1L).quantity(2).build()))
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderReferenceId").value("REF-100"))
                .andExpect(jsonPath("$.description").value("Test order"))
                .andExpect(jsonPath("$.totalAmount").value(25.5));

        verify(orderService).createOrder(any(OrderDTO.class));
    }

    @Test
    @DisplayName("PUT /api/orders/cancel/{id} returns success message when cancellation succeeds")
    void cancelOrder_whenSuccessful_returnsMessage() throws Exception {
        when(orderService.cancelOrder(5L)).thenReturn(true);

        mockMvc.perform(put("/api/orders/cancel/5"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Order is Successfully Cancelled!!"));

        verify(orderService).cancelOrder(5L);
    }

    @Test
    @DisplayName("PUT /api/orders/cancel/{id} returns empty body when cancellation returns false")
    void cancelOrder_whenNotSuccessful_returnsEmptyBody() throws Exception {
        when(orderService.cancelOrder(5L)).thenReturn(false);

        mockMvc.perform(put("/api/orders/cancel/5"))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("PUT /api/orders/status/{id} returns 200 and updated order")
    void updateOrderStatus_returnsOk() throws Exception {
        OrderDTO updated = OrderDTO.builder().id(1L).status("SHIPPED").build();
        when(orderService.updateOrderStatus(eq(1L), any(OrderDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/orders/status/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"SHIPPED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));

        verify(orderService).updateOrderStatus(eq(1L), any(OrderDTO.class));
    }

    @Test
    @DisplayName("GET /api/orders returns list of orders")
    void getAllOrders_withoutStatus_returnsList() throws Exception {
        when(orderService.getAllOrders(isNull())).thenReturn(List.of(
                OrderDTO.builder().id(1L).orderReferenceId("ORD-1").build()));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderReferenceId").value("ORD-1"))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(orderService).getAllOrders(isNull());
    }

    @Test
    @DisplayName("GET /api/orders?status= forwards status to service")
    void getAllOrders_withStatusParam_passesToService() throws Exception {
        when(orderService.getAllOrders("PENDING")).thenReturn(List.of());

        mockMvc.perform(get("/api/orders").param("status", "PENDING"))
                .andExpect(status().isOk());

        verify(orderService).getAllOrders("PENDING");
    }

    @Test
    @DisplayName("GET /api/orders/{id} returns order when found")
    void getOrderById_returns200() throws Exception {
        when(orderService.getOrderById(7L)).thenReturn(
                OrderDTO.builder().id(7L).orderReferenceId("ORD-7").description("d").build());

        mockMvc.perform(get("/api/orders/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.orderReferenceId").value("ORD-7"));

        verify(orderService).getOrderById(7L);
    }

    @Test
    @DisplayName("GET /api/orders/{id} returns 404 when service throws NotFoundException")
    void getOrderById_whenNotFound_returns404() throws Exception {
        when(orderService.getOrderById(99L)).thenThrow(new NotFoundException("Order not found: 99"));

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Order not found: 99"));
    }

    @Test
    @DisplayName("PUT /api/orders/status/{id} returns 400 when service throws OpsNotAllowed")
    void updateOrderStatus_whenInvalid_returns400() throws Exception {
        when(orderService.updateOrderStatus(eq(2L), any(OrderDTO.class)))
                .thenThrow(new OpsNotAllowed("Cannot transition order"));

        mockMvc.perform(put("/api/orders/status/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"DELIVERED\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Cannot transition order"));
    }
}
