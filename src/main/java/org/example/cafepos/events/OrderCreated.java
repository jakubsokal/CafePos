package org.example.cafepos.events;

public record OrderCreated(long orderId) implements OrderEvent { }
