package com.brokagefirm.StockBrokerageSystem.service.impl;

import com.brokagefirm.StockBrokerageSystem.dto.AssetDTO;
import com.brokagefirm.StockBrokerageSystem.entity.Asset;
import com.brokagefirm.StockBrokerageSystem.exception.InsufficientBalanceException;
import com.brokagefirm.StockBrokerageSystem.repository.AssetRepository;
import com.brokagefirm.StockBrokerageSystem.service.AssetServiceTest;
import com.brokagefirm.StockBrokerageSystem.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AssetServiceImplTest implements AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AssetServiceImpl assetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testReserveForBuySuccess() {
        Asset tryAsset = new Asset();
        tryAsset.setUsableSize(500.0);

        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(tryAsset);

        assetService.reserveForBuy(1L, 5.0, 50.0); // 250 TRY

        assertEquals(250.0, tryAsset.getUsableSize());
        verify(assetRepository).save(tryAsset);
    }

    @Test
    public void testReserveForBuyInsufficientBalance() {
        Asset tryAsset = new Asset();
        tryAsset.setUsableSize(100.0);

        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(tryAsset);

        assertThrows(InsufficientBalanceException.class, () -> {
            assetService.reserveForBuy(1L, 5.0, 50.0);
        });
    }

    @Test
    public void testReturnForBuy() {
        Asset tryAsset = new Asset();
        tryAsset.setUsableSize(100.0);

        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(tryAsset);

        assetService.returnForBuy(1L, 2.0, 10.0);

        assertEquals(120.0, tryAsset.getUsableSize());
        verify(assetRepository).save(tryAsset);
    }

    @Test
    public void testFinalizeSell() {
        Asset tryAsset = new Asset();
        tryAsset.setSize(100.0);
        tryAsset.setUsableSize(50.0);

        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(tryAsset);

        assetService.finalizeSell(1L, 3.0, 10.0); // 30 TRY

        assertEquals(130.0, tryAsset.getSize());
        assertEquals(80.0, tryAsset.getUsableSize());
        verify(assetRepository).save(tryAsset);
    }

    @Test
    public void testListAssets() {
        Asset asset = new Asset();
        asset.setCustomerId(1L);
        asset.setAssetName("GARAN");
        List<Asset> assetList = List.of(asset);

        when(assetRepository.findByCustomerId(1L)).thenReturn(assetList);
        when(modelMapper.map(any(), eq(AssetDTO.class))).thenReturn(new AssetDTO());

        List<AssetDTO> result = assetService.listAssets(1L);
        assertEquals(1, result.size());
    }

    @Test
    public void testReserveForSellInsufficientBalance() {
        Asset asset = new Asset();
        asset.setUsableSize(2.0);

        when(assetRepository.findByCustomerIdAndAssetName(1L, "GARAN")).thenReturn(asset);

        assertThrows(InsufficientBalanceException.class, () -> {
            assetService.reserveForSell(1L, "GARAN", 5.0);
        });
    }

    @Test
    public void testReturnForSell() {
        Asset asset = new Asset();
        asset.setUsableSize(5.0);

        when(assetRepository.findByCustomerIdAndAssetName(1L, "GARAN")).thenReturn(asset);

        assetService.returnForSell(1L, "GARAN", 3.0);

        assertEquals(8.0, asset.getUsableSize());
        verify(assetRepository).save(asset);
    }

    @Test
    public void testFinalizeBuyWhenAssetExists() {
        Asset asset = new Asset();
        asset.setSize(10.0);
        asset.setUsableSize(5.0);

        when(assetRepository.findByCustomerIdAndAssetName(1L, "GARAN")).thenReturn(asset);

        assetService.finalizeBuy(1L, "GARAN", 5.0);

        assertEquals(15.0, asset.getSize());
        assertEquals(10.0, asset.getUsableSize());
        verify(assetRepository).save(asset);
    }

    @Test
    public void testFinalizeSellWhenAssetNotFound() {
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(null);

        assertThrows(InsufficientBalanceException.class, () -> {
            assetService.finalizeSell(1L, 2.0, 5.0);
        });
    }
}