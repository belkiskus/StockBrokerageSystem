package com.brokagefirm.StockBrokerageSystem.service.impl;

import com.brokagefirm.StockBrokerageSystem.dto.AssetDTO;
import com.brokagefirm.StockBrokerageSystem.entity.Asset;
import com.brokagefirm.StockBrokerageSystem.exception.InsufficientBalanceException;
import com.brokagefirm.StockBrokerageSystem.repository.AssetRepository;
import com.brokagefirm.StockBrokerageSystem.service.AssetService;
import com.brokagefirm.StockBrokerageSystem.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetServiceImpl implements AssetService {

    private static final Logger logger = LoggerFactory.getLogger(AssetService.class);

    private final AssetRepository assetRepository;
    private final CustomerService customerService;
    private final ModelMapper modelMapper;

    public AssetServiceImpl(AssetRepository assetRepository,CustomerService customerService, ModelMapper modelMapper) {
        this.assetRepository = assetRepository;
        this.customerService = customerService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public void reserveForBuy(Long customerId, Double orderSize, Double orderPrice) {
        logger.info("reserveForBuy -> customerId={}, needed={} TRY", customerId, (orderSize * orderPrice));

        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY");
        double requiredAmount = orderSize * orderPrice;
        if (tryAsset == null || tryAsset.getUsableSize() < requiredAmount) {
            logger.error("Insufficient TRY balance for BUY. required={}, available={}",
                    requiredAmount, tryAsset != null ? tryAsset.getUsableSize() : 0.0);
            throw new InsufficientBalanceException("Yetersiz TRY bakiyesi.");
        }

        tryAsset.setUsableSize(tryAsset.getUsableSize() - requiredAmount);
        assetRepository.save(tryAsset);
    }

    @Transactional
    public void reserveForSell(Long customerId, String assetName, Double orderSize) {
        logger.info("reserveForSell -> customerId={}, assetName={}, size={}", customerId, assetName, orderSize);

        Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName);
        if (asset == null || asset.getUsableSize() < orderSize) {
            logger.error("Insufficient asset balance for SELL. asset={}, required={}, available={}",
                    assetName, orderSize, asset != null ? asset.getUsableSize() : 0.0);
            throw new InsufficientBalanceException("Yetersiz hisse bakiyesi: " + assetName);
        }

        asset.setUsableSize(asset.getUsableSize() - orderSize);
        assetRepository.save(asset);
    }

    @Transactional
    public void returnForBuy(Long customerId, Double orderSize, Double orderPrice) {
        logger.info("returnForBuy -> customerId={}, amount={}", customerId, (orderSize * orderPrice));

        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY");
        if (tryAsset != null) {
            double total = orderSize * orderPrice;
            tryAsset.setUsableSize(tryAsset.getUsableSize() + total);
            assetRepository.save(tryAsset);
        }
    }

    @Transactional
    public void returnForSell(Long customerId, String assetName, Double orderSize) {
        logger.info("returnForSell -> customerId={}, assetName={}, size={}", customerId, assetName, orderSize);

        Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName);
        if (asset != null) {
            asset.setUsableSize(asset.getUsableSize() + orderSize);
            assetRepository.save(asset);
        }
    }

    @Transactional
    public void finalizeBuy(Long customerId, String assetName, Double orderSize) {
        logger.info("finalizeBuy -> customerId={}, assetName={}, size={}", customerId, assetName, orderSize);

        Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName);
        if (asset == null) {
            asset = new Asset();
            asset.setCustomerId(customerId);
            asset.setAssetName(assetName);
            asset.setSize(0.0);
            asset.setUsableSize(0.0);
        }
        asset.setSize(asset.getSize() + orderSize);
        asset.setUsableSize(asset.getUsableSize() + orderSize);
        assetRepository.save(asset);
    }

    @Transactional
    public void finalizeSell(Long customerId, Double orderSize, Double orderPrice) {
        logger.info("finalizeSell -> customerId={}, income={}", customerId, (orderSize * orderPrice));

        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY");
        if (tryAsset == null) {
            logger.error("TRY asset not found for customer {}", customerId);
            throw new InsufficientBalanceException("TRY asset not found.");
        }
        double income = orderSize * orderPrice;
        tryAsset.setSize(tryAsset.getSize() + income);
        tryAsset.setUsableSize(tryAsset.getUsableSize() + income);
        assetRepository.save(tryAsset);
    }

    public List<AssetDTO> listAssets(Long customerId) {
        List<Asset> assets = assetRepository.findByCustomerId(customerId);
        return assets.stream().map(asset -> {
            AssetDTO assetDto = modelMapper.map(asset, AssetDTO.class);
            customerService.findById(asset.getCustomerId())
                    .ifPresentOrElse(
                            customer -> assetDto.setCustomerUsername(customer.getUsername()),
                            () -> assetDto.setCustomerUsername("Unknown Username")
                    );
            return assetDto;
        }).collect(Collectors.toList());
    }
}
