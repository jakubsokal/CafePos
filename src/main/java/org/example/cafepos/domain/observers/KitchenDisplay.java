package org.example.cafepos.domain.observers;

import org.example.cafepos.domain.Order;
import org.example.cafepos.domain.OrderObserver;

public final class KitchenDisplay implements OrderObserver {
    private final String ITEM_ADDED_EVENT = "itemAdded";

    private final String PAID_EVENT = "paid";

    @Override
    public void updated(Order order, String eventType) {
        //Updates the kitchen display when an item is added or payment is received
        switch (eventType) {
            case ITEM_ADDED_EVENT -> System.out.printf("[Kitchen] Order #%d %dx %s added.%n",
                    order.id(), order.items().getLast().quantity(), order.items().getLast().product().name());
            case PAID_EVENT -> System.out.printf("[Kitchen] Order #%d: Payment received.%n", order.id());
        }
    }
}
