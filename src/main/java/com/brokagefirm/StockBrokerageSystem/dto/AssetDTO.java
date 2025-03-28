package com.brokagefirm.StockBrokerageSystem.dto;

public class AssetDTO {

    private String customerUsername;
    private String assetName;
    private int size;
    private int usableSize;

    public AssetDTO() {
    }

    public AssetDTO(String customerUsername, String assetName, int size, int usableSize) {
        this.customerUsername = customerUsername;
        this.assetName = assetName;
        this.size = size;
        this.usableSize = usableSize;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getUsableSize() {
        return usableSize;
    }

    public void setUsableSize(int usableSize) {
        this.usableSize = usableSize;
    }
}
