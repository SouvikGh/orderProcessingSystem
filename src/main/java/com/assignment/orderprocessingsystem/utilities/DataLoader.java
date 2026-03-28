package com.assignment.orderprocessingsystem.utilities;

import com.assignment.orderprocessingsystem.entities.Item;
import com.assignment.orderprocessingsystem.repositories.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;
@Configuration
public class DataLoader {
    @Bean
    CommandLineRunner seedItems(ItemRepository itemsRepository) {
        return args -> {
            if (itemsRepository.count() > 0) {
                return;
            }

            List<Item> items = List.of(
                    new Item(null, "Laptop", BigDecimal.valueOf(64999.00) , "14-inch business laptop"),
                    new Item(null, "Mouse", BigDecimal.valueOf(899.00), "Wireless optical mouse"),
                    new Item(null, "Keyboard", BigDecimal.valueOf(1499.00), "Mechanical keyboard"),
                    new Item(null, "Monitor", BigDecimal.valueOf(12499.00), "24-inch full HD monitor"),
                    new Item(null, "USB-C Hub", BigDecimal.valueOf(2199.00), "Multiport USB-C hub")
            );
            itemsRepository.saveAll(items);
        };
    }
}
