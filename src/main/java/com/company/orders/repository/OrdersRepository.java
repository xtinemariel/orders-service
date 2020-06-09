package com.company.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.company.orders.model.OrderData;

@Repository
public interface OrdersRepository extends JpaRepository<OrderData, Long> {

}
