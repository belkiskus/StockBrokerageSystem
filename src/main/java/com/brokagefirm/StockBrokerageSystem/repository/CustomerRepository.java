package com.brokagefirm.StockBrokerageSystem.repository;

import com.brokagefirm.StockBrokerageSystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByUsername(String username);
    boolean existsByUsername(String username);
}
