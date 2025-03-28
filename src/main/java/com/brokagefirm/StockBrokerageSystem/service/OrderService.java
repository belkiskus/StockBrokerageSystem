package com.brokagefirm.StockBrokerageSystem.service;

import com.brokagefirm.StockBrokerageSystem.dto.OrderDTO;
import com.brokagefirm.StockBrokerageSystem.entity.Order;
import com.brokagefirm.StockBrokerageSystem.entity.enums.OrderSide;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    Order createOrder(Long customerId, String assetName, OrderSide side, Double size, Double price);
    List<OrderDTO> listOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
    void cancelOrder(Long orderId);
    Order matchOrder(Long orderId);
}
