package org.example.cafepos.checkout.discount;

import org.example.cafepos.common.Money;

public interface DiscountPolicy {
    Money discountOf(Money subtotal);
}
