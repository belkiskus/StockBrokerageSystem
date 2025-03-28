package com.brokagefirm.StockBrokerageSystem.controller;

import com.brokagefirm.StockBrokerageSystem.entity.Order;
import com.brokagefirm.StockBrokerageSystem.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{orderId}/match")
    @Operation(
            summary = "Match an order by ID",
            description = "Matchs an existing order based on the provided order ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order successfully matched"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @Schema(description = "Match an order by ID")
    @PreAuthorize("hasRole('ADMIN')")
    public Order matchOrder(@PathVariable Long orderId) {
        return orderService.matchOrder(orderId);
    }

}

