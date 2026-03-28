package com.assignment.orderprocessingsystem.dataObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {
    private Long id;
    private String orderReferenceId;
    private String status;
    private String description;
    private List<ItemDTO> items = new ArrayList<>();
    private BigDecimal totalAmount;
    private String createdAt;
}