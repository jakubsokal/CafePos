package org.example.cafepos.events;

public sealed interface OrderEvent permits OrderCreated, OrderPaid { }

