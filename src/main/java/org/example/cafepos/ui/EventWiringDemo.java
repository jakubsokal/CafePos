package org.example.cafepos.ui;

import org.example.cafepos.events.*;
import org.example.cafepos.infra.Wiring;

public final class EventWiringDemo {
    public static void main(String[] args) {
        long id = 4201L;
        var bus = new EventBus();
        var comp = Wiring.createDefault();
        var controller = new OrderController(comp.repo(),
                comp.checkout());

        bus.on(OrderCreated.class, e -> System.out.println("[UI] order created: " + e.orderId()));
        bus.on(OrderPaid.class, e -> System.out.println("[UI] order paid: " + e.orderId()));

        controller.createOrder(id);
        bus.emit(new OrderCreated(id));
// after a payment in your real flow:
        bus.emit(new OrderPaid(id));
    }
}