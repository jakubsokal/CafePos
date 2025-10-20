package org.example.cafepos.checkout;

import org.example.cafepos.checkout.discount.DiscountPolicy;
import org.example.cafepos.checkout.tax.TaxPolicy;
import org.example.cafepos.common.Money;

public class PricingService {
    private final DiscountPolicy discountPolicy;
    private final TaxPolicy taxPolicy;

    public PricingService(DiscountPolicy discountPolicy, TaxPolicy taxPolicy) {
        this.discountPolicy = discountPolicy;
        this.taxPolicy = taxPolicy;
    }

    public PricingResult price(Money subtotal) {
        Money discount = discountPolicy.discountOf(subtotal);
        Money discounted = Money.of(subtotal.asBigDecimal()
                .subtract(discount.asBigDecimal()));

        if (discounted.asBigDecimal().signum() < 0) discounted = Money.zero();
        Money tax = taxPolicy.taxOn(discounted);
        Money total = discounted.add(tax);

        return new PricingResult(subtotal, discount, tax, total);
    }

    public static record PricingResult
            (Money subtotal, Money discount, Money tax, Money total){}
}
