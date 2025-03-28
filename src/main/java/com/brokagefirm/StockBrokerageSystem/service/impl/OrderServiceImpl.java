package com.brokagefirm.StockBrokerageSystem.service.impl;

import com.brokagefirm.StockBrokerageSystem.dto.OrderDTO;
import com.brokagefirm.StockBrokerageSystem.entity.Order;
import com.brokagefirm.StockBrokerageSystem.entity.enums.OrderSide;
import com.brokagefirm.StockBrokerageSystem.entity.enums.OrderStatus;
import com.brokagefirm.StockBrokerageSystem.exception.OrderNotFoundException;
import com.brokagefirm.StockBrokerageSystem.repository.OrderRepository;
import com.brokagefirm.StockBrokerageSystem.service.AssetService;
import com.brokagefirm.StockBrokerageSystem.service.OrderService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final AssetService assetService;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository, AssetService assetService, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.assetService = assetService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public Order createOrder(Long customerId, String assetName, OrderSide side, Double size, Double price) {
        logger.info("createOrder -> customerId={}, assetName={}, side={}, size={}, price={}",
                customerId, assetName, side, size, price);

        if (side == OrderSide.BUY) {
            assetService.reserveForBuy(customerId, size, price);
        } else if (side == OrderSide.SELL) {
            assetService.reserveForSell(customerId, assetName, size);
        }

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setAssetName(assetName);
        order.setOrderSide(side);
        order.setSize(size);
        order.setPrice(price);
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now());

        order = orderRepository.save(order);
        logger.info("Order created with ID={}, status={}", order.getId(), order.getStatus());
        return order;
    }

    public List<OrderDTO> listOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("listOrders -> customerId={}, startDate={}, endDate={}", customerId, startDate, endDate);

        if (startDate == null) {
            startDate = LocalDateTime.now().minusYears(10);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        List<Order> orders = orderRepository.findByCustomerIdAndCreateDateBetween(
                customerId, startDate, endDate
        );
        logger.info("Found {} orders for customerId={}", orders.size(), customerId);
        Type listType = new TypeToken<List<OrderDTO>>() {}.getType();
        return modelMapper.map(orders, listType);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        logger.info("cancelOrder -> orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found for ID=" + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            logger.error("Cannot cancel order with status={}", order.getStatus());
            throw new RuntimeException("Only orders with PENDING status can be canceled.");
        }

        if (order.getOrderSide() == OrderSide.BUY) {
            assetService.returnForBuy(order.getCustomerId(), order.getSize(), order.getPrice());
        } else if (order.getOrderSide() == OrderSide.SELL) {
            assetService.returnForSell(order.getCustomerId(), order.getAssetName(), order.getSize());
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
        logger.info("Order with ID={} canceled successfully.", orderId);
    }

    @Transactional
    public Order matchOrder(Long orderId) {
        logger.info("matchOrder -> orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found for ID=" + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            logger.error("Cannot match order with status={}", order.getStatus());
            throw new RuntimeException("Only PENDING orders can be matched.");
        }

        if (order.getOrderSide() == OrderSide.BUY) {
            assetService.finalizeBuy(order.getCustomerId(), order.getAssetName(), order.getSize());
        } else if (order.getOrderSide() == OrderSide.SELL) {
            assetService.finalizeSell(order.getCustomerId(), order.getSize(), order.getPrice());
        }

        order.setStatus(OrderStatus.MATCHED);
        order = orderRepository.save(order);
        logger.info("Order with ID={} matched successfully.", orderId);
        return order;
    }

}
