package org.example.cafepos.domain.observers;

import org.example.cafepos.domain.Order;
import org.example.cafepos.domain.OrderObserver;

public final class DeliveryDesk implements OrderObserver {

    @Override
    public void updated(Order order, String eventType) {
        //Notifies the delivery desk when an order is ready
        String READY_EVENT = "ready";

        if (READY_EVENT.equals(eventType)) {
            System.out.printf("[Delivery] Order #%d is ready for delivery.%n", order.id());
        }
    }
}
