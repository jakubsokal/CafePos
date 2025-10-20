package org.example.cafepos.checkout.discount;

import org.example.cafepos.common.Money;

public class FixedCouponDiscount implements DiscountPolicy{
    private final Money amount;

    public FixedCouponDiscount(Money amount) {
        this.amount = amount;
    }

    @Override public Money discountOf(Money subtotal) {
// cap at subtotal
        if (amount.asBigDecimal().compareTo(subtotal.asBigDecimal()) > 0)
            return subtotal;

        return amount;
    }
}
