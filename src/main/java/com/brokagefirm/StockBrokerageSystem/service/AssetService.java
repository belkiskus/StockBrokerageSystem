package com.brokagefirm.StockBrokerageSystem.service;

import com.brokagefirm.StockBrokerageSystem.dto.AssetDTO;

import java.util.List;

public interface AssetService {

    void reserveForBuy(Long customerId, Double orderSize, Double orderPrice);
    void reserveForSell(Long customerId, String assetName, Double orderSize);
    void returnForBuy(Long customerId, Double orderSize, Double orderPrice);
    void returnForSell(Long customerId, String assetName, Double orderSize);
    void finalizeBuy(Long customerId, String assetName, Double orderSize);
    void finalizeSell(Long customerId, Double orderSize, Double orderPrice);
    List<AssetDTO> listAssets(Long customerId);
}
