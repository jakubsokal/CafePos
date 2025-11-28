package org.example.cafepos.events;

public record OrderPaid(long orderId) implements OrderEvent { }
