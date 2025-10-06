package org.example.cafepos.demo;

import org.example.cafepos.catalog.Catalog;
import org.example.cafepos.catalog.InMemoryCatalog;
import org.example.cafepos.catalog.SimpleProduct;
import org.example.cafepos.common.Money;
import org.example.cafepos.domain.LineItem;
import org.example.cafepos.domain.Order;
import org.example.cafepos.domain.OrderIds;
import org.example.cafepos.domain.observers.CustomerNotifier;
import org.example.cafepos.domain.observers.DeliveryDesk;
import org.example.cafepos.domain.observers.KitchenDisplay;
import org.example.cafepos.payment.CardPayment;
import org.example.cafepos.payment.CashPayment;
import org.example.cafepos.payment.WalletPayment;

public final class Week4Demo {
    public static void main(String[] args) {
        Catalog catalog = new InMemoryCatalog();
        catalog.add(new SimpleProduct("P-ESP", "Espresso",
                Money.of(2.50)));
        catalog.add(new SimpleProduct("P-CCK", "Chocolate Cookie",
                Money.of(3.50)));
        catalog.add(new SimpleProduct("P-BCK", "Biscuit Cake",
                Money.of(5.50)));

        Order order = new Order(OrderIds.next());
        order.register(new KitchenDisplay());
        order.register(new DeliveryDesk());
        order.register(new CustomerNotifier());
        order.addItem(new LineItem(catalog.findById("P-ESP")
                .orElseThrow(), 1));
        order.pay(new CashPayment());
        order.markReady();

        Order order1 = new Order(OrderIds.next());
        order1.register(new KitchenDisplay());
        order1.register(new DeliveryDesk());
        order1.register(new CustomerNotifier());
        order1.addItem(new LineItem(catalog.findById("P-ESP")
                .orElseThrow(), 2));
        order1.addItem(new LineItem(catalog.findById("P-CCK")
                .orElseThrow(), 4));
        order1.pay(new CardPayment("1234567890"));
        order1.markReady();

        Order order2 = new Order(OrderIds.next());
        KitchenDisplay kitchenDisplay = new KitchenDisplay();
        order2.register(kitchenDisplay);
        order2.register(new DeliveryDesk());
        order2.register(new CustomerNotifier());
        order2.addItem(new LineItem(catalog.findById("P-ESP")
                .orElseThrow(), 2));
        order2.addItem(new LineItem(catalog.findById("P-CCK")
                .orElseThrow(), 1));
        order2.addItem(new LineItem(catalog.findById("P-BCK")
                .orElseThrow(), 4));
        order2.pay(new WalletPayment("jakub-wallet-09"));
        order2.markReady();
        order2.unregister(kitchenDisplay);
    }
}
