package org.example.cafepos.demo;


import org.example.cafepos.catalog.Catalog;
import org.example.cafepos.catalog.InMemoryCatalog;
import org.example.cafepos.catalog.SimpleProduct;
import org.example.cafepos.common.Money;
import org.example.cafepos.domain.LineItem;
import org.example.cafepos.domain.Order;
import org.example.cafepos.domain.OrderIds;
import org.example.cafepos.payment.CashPayment;
import org.example.cafepos.payment.CardPayment;

public final class Week3Demo {
    public static void main(String[] args) {
        Catalog catalog = new InMemoryCatalog();

        catalog.add(new SimpleProduct(
                "P-ESP", "Espresso", Money.of(2.50)
        ));

        catalog.add(new SimpleProduct(
                "P-CCK", "Chocolate Cookie", Money.of(3.50)
        ));

        // Cash payment
        Order order1 = new Order(OrderIds.next());
        order1.addItem(new LineItem(catalog.findById("P-ESP")
                .orElseThrow(), 2)
        );

        order1.addItem(new LineItem(catalog.findById("P-CCK")
                .orElseThrow(), 1)
        );

        System.out.println("Order #" + order1.id() + " Total: " +
                order1.totalWithTax(10)
        );

        order1.pay(new CashPayment());

        // Card payment
        Order order2 = new Order(OrderIds.next());
        order2.addItem(new LineItem(catalog.findById("P-ESP")
                .orElseThrow(), 2)
        );

        order2.addItem(new LineItem(catalog.findById("P-CCK")
                .orElseThrow(), 1)
        );

        System.out.println("Order #" + order2.id() + " Total: " +
                order2.totalWithTax(10)
        );

        order2.pay(new CardPayment("1234567812341234"));
    }
}
