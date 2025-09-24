package org.example.cafepos.domain;

public interface PaymentStrategy {
    void pay(Order order);
}
