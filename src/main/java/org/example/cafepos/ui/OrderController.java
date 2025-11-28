package org.example.cafepos.ui;

import org.example.cafepos.app.CheckoutService;
import org.example.cafepos.domain.LineItem;
import org.example.cafepos.domain.Order;
import org.example.cafepos.domain.OrderRepository;
import org.example.cafepos.factory.ProductFactory;

public final class OrderController {
    private final OrderRepository repo;
    private final CheckoutService checkout;
    private final ProductFactory factory = new ProductFactory();

    public OrderController(OrderRepository repo,
                           CheckoutService checkout) {
        this.repo = repo;
        this.checkout = checkout;
    }

    public long createOrder(long id) {
        repo.save(new Order(id));

        return id;
    }

    public void addItem(long orderId, String recipe, int qty) {
        Order order = repo.findById(orderId).orElseThrow();
        order.addItem(new LineItem(factory.create(recipe), qty));

        repo.save(order);
    }

    public String checkout(long orderId, int taxPercent) {
        return checkout.checkout(orderId, taxPercent);
    }

}
