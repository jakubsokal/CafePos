package org.example.cafepos.checkout.service;

import org.example.cafepos.checkout.PricingService;
import org.example.cafepos.checkout.ReceiptPrinter;
import org.example.cafepos.common.Money;
import org.example.cafepos.common.Priced;
import org.example.cafepos.common.Product;
import org.example.cafepos.domain.LineItem;
import org.example.cafepos.domain.Order;
import org.example.cafepos.domain.OrderIds;
import org.example.cafepos.factory.ProductFactory;
import org.example.cafepos.payment.PaymentStrategy;

public final class CheckoutService {
    private final ProductFactory factory;
    private final PricingService pricing;
    private final ReceiptPrinter printer;

    public static int TAX_PERCENT = 10;

    public static String LAST_DISCOUNT_CODE = null;
    private final int taxPercent;

    //3.5)  **Inject dependencies** (factory, discount policy, tax policy, printer, payment strategy)via constructor.
    public CheckoutService(ProductFactory factory, PricingService pricing, ReceiptPrinter printer, int taxPercent) {
        this.factory = factory;
        this.pricing = pricing;
        this.printer = printer;
        this.taxPercent = taxPercent;
    }

    public String checkout(String recipe, int qty, PaymentStrategy paymentStrategy) {
        Product product = factory.create(recipe);
        if (qty <= 0) qty = 1;

        Money unit = (product instanceof Priced p) ? p.price() : product.basePrice();
        Money subtotal = unit.multiply(qty);
        var result = pricing.price(subtotal);

        // 3.4) Handle payment - **Replace paymentType string switch** with a call to an injected `PaymentStrategy
        if (paymentStrategy != null) {
            // Create the real Order
            Order order = new Order(OrderIds.next());
            order.addItem(new LineItem(product, qty));

            // Adapt to your Week-3 signature; if your strategy expects an Order, pass the real one here.
            // If your strategy prints based on totals, wrap in a tiny adapter and call after pricing.
            paymentStrategy.pay(order);
        }
        return printer.format(recipe, qty, result, taxPercent);
    }
}
