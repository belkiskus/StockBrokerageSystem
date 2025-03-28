package com.brokagefirm.StockBrokerageSystem.service;

import com.brokagefirm.StockBrokerageSystem.entity.Customer;

import java.util.Optional;

public interface CustomerService {

    Optional<Customer> findById(Long id);
}
