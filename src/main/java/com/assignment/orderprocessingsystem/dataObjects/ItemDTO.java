package com.assignment.orderprocessingsystem.dataObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDTO {
    private Long id;
    private String itemName;
    private BigDecimal unitPrice;
    private BigDecimal unitPriceAtOrderTime;
    private Integer quantity;
    private BigDecimal totalItemAmount;

}
