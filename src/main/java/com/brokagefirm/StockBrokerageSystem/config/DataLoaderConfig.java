package com.brokagefirm.StockBrokerageSystem.config;

import com.brokagefirm.StockBrokerageSystem.entity.Customer;
import com.brokagefirm.StockBrokerageSystem.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoaderConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataLoaderConfig.class);

    @Bean
    public CommandLineRunner loadAdminData(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!customerRepository.existsByUsername("admin")) {
                Customer admin = new Customer();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");
                customerRepository.save(admin);
                logger.info("Admin user created in Customer table.");
            } else {
                logger.info("Admin user already exists in Customer table.");
            }
        };
    }
}
