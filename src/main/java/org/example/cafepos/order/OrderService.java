package org.example.cafepos.order;

import org.example.cafepos.common.Money;
import org.example.cafepos.common.Product;
import org.example.cafepos.domain.LineItem;
import org.example.cafepos.domain.Order;
import org.example.cafepos.factory.ProductFactory;
import org.example.cafepos.payment.PaymentStrategy;


public final class OrderService {
    private final ProductFactory factory = new ProductFactory();
    private final Order order;
    public OrderService(Order order) { this.order = order; }
    public void addItem(String recipe, int qty) {
        Product p = factory.create(recipe);
        order.addItem(new LineItem(p, qty));
        System.out.println("[Service] Added " + p.name() + " x" + qty);
    }
    //added this method to Order to remove reflection
    public void removeLastItem() {
        order.removeLastItem();
        System.out.println("[Service] Removed last item");
    }
    public Money totalWithTax(int percent) { return
            order.totalWithTax(percent); }
    public void pay(PaymentStrategy strategy, int taxPercent) {
// Usually you'd call order.pay(strategy), here we display apayment using the computed total
        var total = order.totalWithTax(taxPercent);
        strategy.pay(order);
        System.out.println("[Service] Payment processed for total " +
                total);
    }
    public Order order() { return order; }
}
