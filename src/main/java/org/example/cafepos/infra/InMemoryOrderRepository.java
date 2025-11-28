package org.example.cafepos.infra;

import org.example.cafepos.domain.Order;
import org.example.cafepos.domain.OrderRepository;

import java.util.*;
public final class InMemoryOrderRepository implements OrderRepository {
    private final Map<Long, Order> store = new HashMap<>();

    @Override public void save(Order order) {
        store.put(order.id(), order);
    }

    @Override public Optional<Order> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }
}