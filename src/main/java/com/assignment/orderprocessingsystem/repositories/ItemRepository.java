package com.assignment.orderprocessingsystem.repositories;

import com.assignment.orderprocessingsystem.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
