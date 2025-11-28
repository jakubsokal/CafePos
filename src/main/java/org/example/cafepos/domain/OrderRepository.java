package org.example.cafepos.domain;
import org.example.cafepos.domain.Order;

import java.util.Optional;
public interface OrderRepository {
    void save(Order order);

    Optional<Order> findById(long id);
}