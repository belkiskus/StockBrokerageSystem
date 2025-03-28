package com.brokagefirm.StockBrokerageSystem.service;

public interface OrderServiceTest {

    void testCreateOrderBuy();
    void testListOrders();
    void testCancelOrderSuccess();
    void testCancelOrderNotFound();
    void testMatchOrderBuy();
    void testMatchOrderWrongStatus();

}
