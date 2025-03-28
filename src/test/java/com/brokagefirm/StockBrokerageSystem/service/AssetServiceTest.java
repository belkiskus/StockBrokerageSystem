package com.brokagefirm.StockBrokerageSystem.service;

public interface AssetServiceTest {

    void testReserveForBuySuccess();
    void testReserveForBuyInsufficientBalance();
    void testReturnForBuy();
    void testFinalizeSell();
    void testListAssets();
    void testReserveForSellInsufficientBalance();
    void testReturnForSell();
    void testFinalizeBuyWhenAssetExists();
    void testFinalizeSellWhenAssetNotFound();
}
