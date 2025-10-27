package org.example.cafepos.checkout.discount;

import org.example.cafepos.common.Money;

public final class NoDiscount implements DiscountPolicy{
    @Override
    public Money discountOf(Money subtotal) {
        return Money.zero();
    }
}
