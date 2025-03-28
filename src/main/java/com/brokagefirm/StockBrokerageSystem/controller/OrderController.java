package com.brokagefirm.StockBrokerageSystem.controller;

import com.brokagefirm.StockBrokerageSystem.dto.OrderDTO;
import com.brokagefirm.StockBrokerageSystem.entity.Order;
import com.brokagefirm.StockBrokerageSystem.entity.enums.OrderSide;
import com.brokagefirm.StockBrokerageSystem.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders API", description = "API for managing orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    @Operation(
            summary = "Create a new order",
            description = "Creates a new order based on provided customer details and order information."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order successfully created"),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid input")
    })
    @Schema(description = "Create a new order")
    @PreAuthorize("hasAnyRole('ADMIN','USER') and @customerSecurityService.isCustomerOwner(#customerId)")
    public Order createOrder(@RequestParam Long customerId,
                             @RequestParam String assetName,
                             @RequestParam OrderSide orderSide,  // Enum kullanımı
                             @RequestParam Double size,
                             @RequestParam double price) {
        return orderService.createOrder(customerId, assetName, orderSide, size, price);
    }

    @GetMapping("/list")
    @Operation(
            summary = "List all orders for a customer",
            description = "Retrieves a list of orders based on customer ID and date range."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched orders"),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid parameters")
    })
    @Schema(description = "List orders by customer and date range")
    @PreAuthorize("hasAnyRole('ADMIN','USER') and @customerSecurityService.isCustomerOwner(#customerId)")
    public List<OrderDTO> listOrders(@RequestParam Long customerId,
                                     @RequestParam LocalDateTime startDate,
                                     @RequestParam LocalDateTime endDate) {
        return orderService.listOrders(customerId, startDate, endDate);
    }

    @DeleteMapping("/cancel/{orderId}")
    @Operation(
            summary = "Cancel an order by ID",
            description = "Cancels an existing order based on the provided order ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order successfully canceled"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @Schema(description = "Cancel an order by ID")
    @PreAuthorize("hasAnyRole('ADMIN','USER') and @customerSecurityService.isCustomerOwner(#customerId)")
    public void cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
    }

}
