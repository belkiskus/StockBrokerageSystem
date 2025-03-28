package com.brokagefirm.StockBrokerageSystem.service.impl;

import com.brokagefirm.StockBrokerageSystem.entity.Customer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CustomerSecurityService {

    public boolean isCustomerOwner(Long customerId) {
        Customer currentUser = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getId().equals(customerId);
    }
}
