package com.brokagefirm.StockBrokerageSystem.service.impl;

import com.brokagefirm.StockBrokerageSystem.dto.OrderDTO;
import com.brokagefirm.StockBrokerageSystem.entity.Order;
import com.brokagefirm.StockBrokerageSystem.entity.enums.OrderSide;
import com.brokagefirm.StockBrokerageSystem.entity.enums.OrderStatus;
import com.brokagefirm.StockBrokerageSystem.exception.OrderNotFoundException;
import com.brokagefirm.StockBrokerageSystem.repository.OrderRepository;
import com.brokagefirm.StockBrokerageSystem.service.AssetService;
import com.brokagefirm.StockBrokerageSystem.service.OrderServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderServiceImplTest implements OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetService assetService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrderBuy() {
        Long customerId = 1L;
        String assetName = "GARAN";
        OrderSide side = OrderSide.BUY;
        Double size = 10.0;
        Double price = 20.0;

        Order mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setCustomerId(customerId);
        mockOrder.setAssetName(assetName);
        mockOrder.setOrderSide(side);
        mockOrder.setSize(size);
        mockOrder.setPrice(price);
        mockOrder.setStatus(OrderStatus.PENDING);
        mockOrder.setCreateDate(LocalDateTime.now());

        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        Order result = orderService.createOrder(customerId, assetName, side, size, price);

        verify(assetService).reserveForBuy(customerId, size, price);
        verify(orderRepository).save(any(Order.class));

        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(assetName, result.getAssetName());
    }

    @Test
    public void testListOrders() {
        Long customerId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        LocalDateTime endDate = LocalDateTime.now();

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setAssetName("GARAN");
        order.setOrderSide(OrderSide.BUY);
        order.setSize(5.0);
        order.setPrice(15.0);
        order.setCreateDate(LocalDateTime.now());

        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate)).thenReturn(orders);

        Type listType = new TypeToken<List<OrderDTO>>() {}.getType();
        when(modelMapper.map(orders, listType)).thenReturn(List.of(new OrderDTO()));

        List<OrderDTO> result = orderService.listOrders(customerId, startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testCancelOrderSuccess() {
        Order order = new Order();
        order.setId(1L);
        order.setCustomerId(1L);
        order.setOrderSide(OrderSide.SELL);
        order.setStatus(OrderStatus.PENDING);
        order.setAssetName("GARAN");
        order.setSize(5.0);
        order.setPrice(20.0);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        verify(assetService).returnForSell(1L, "GARAN", 5.0);
        verify(orderRepository).save(order);
        assertEquals(OrderStatus.CANCELED, order.getStatus());
    }

    @Test
    public void testCancelOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    public void testMatchOrderBuy() {
        Order order = new Order();
        order.setId(1L);
        order.setCustomerId(1L);
        order.setOrderSide(OrderSide.BUY);
        order.setStatus(OrderStatus.PENDING);
        order.setAssetName("GARAN");
        order.setSize(2.0);
        order.setPrice(10.0);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.matchOrder(1L);

        verify(assetService).finalizeBuy(1L, "GARAN", 2.0);
        verify(orderRepository).save(order);
        assertEquals(OrderStatus.MATCHED, result.getStatus());
    }

    @Test
    public void testMatchOrderWrongStatus() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.MATCHED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class, () -> orderService.matchOrder(1L));
    }
}
