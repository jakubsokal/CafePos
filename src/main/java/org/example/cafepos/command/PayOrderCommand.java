package org.example.cafepos.command;

import org.example.cafepos.order.OrderService;
import org.example.cafepos.payment.PaymentStrategy;

public final class PayOrderCommand implements Command {
    private final OrderService service;
    private final PaymentStrategy strategy;
    private final int taxPercent;
    public PayOrderCommand(OrderService service, PaymentStrategy
            strategy, int taxPercent) {
        this.service = service; this.strategy = strategy;
        this.taxPercent = taxPercent;
    }
    @Override public void execute() { service.pay(strategy,
            taxPercent); }
}
