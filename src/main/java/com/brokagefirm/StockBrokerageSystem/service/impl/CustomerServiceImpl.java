package com.brokagefirm.StockBrokerageSystem.service.impl;

import com.brokagefirm.StockBrokerageSystem.entity.Customer;
import com.brokagefirm.StockBrokerageSystem.repository.CustomerRepository;
import com.brokagefirm.StockBrokerageSystem.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }
}
