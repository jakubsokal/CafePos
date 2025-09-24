package org.example.cafepos.payment;

import org.example.cafepos.domain.Order;

public interface PaymentStrategy {
    void pay(Order order);
}
