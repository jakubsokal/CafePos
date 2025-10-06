package org.example.cafepos.domain.observers;

import org.example.cafepos.domain.Order;
import org.example.cafepos.domain.OrderObserver;

public final class CustomerNotifier implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        //Updates the customer about the order status
        System.out.printf("[Customer] Dear customer, your Order #%d has been updated: %s.%n",
                order.id(), eventType);
    }
}
