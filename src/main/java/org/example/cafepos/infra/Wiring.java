package org.example.cafepos.infra;

import org.example.cafepos.app.CheckoutService;
import org.example.cafepos.checkout.discount.LoyaltyPercentDiscount;
import org.example.cafepos.checkout.tax.FixedRateTaxPolicy;
import org.example.cafepos.domain.OrderRepository;
import org.example.cafepos.pricing.PricingService;

public final class Wiring {
    public static record Components(OrderRepository repo,
                                    PricingService pricing,
                                    CheckoutService checkout) {}

    public static Components createDefault() {
        OrderRepository repo = new InMemoryOrderRepository();

        PricingService pricing = new PricingService(
                new LoyaltyPercentDiscount(5),
                new FixedRateTaxPolicy(10));

        CheckoutService checkout = new CheckoutService(repo, pricing);

        return new Components(repo, pricing, checkout);
    }
}
