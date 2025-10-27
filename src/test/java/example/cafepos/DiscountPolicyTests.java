package example.cafepos;

import org.example.cafepos.checkout.discount.DiscountPolicy;
import org.example.cafepos.checkout.discount.FixedCouponDiscount;
import org.example.cafepos.checkout.discount.LoyaltyPercentDiscount;
import org.example.cafepos.checkout.discount.NoDiscount;
import org.example.cafepos.common.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiscountPolicyTests {
    @Test
    void loyaltyPercent_calculates_5_percent() {
        DiscountPolicy policy = new LoyaltyPercentDiscount(5);
        Money subtotal = Money.of(10.00);

        Money discount = policy.discountOf(subtotal);

        assertEquals(Money.of(0.5), discount);
    }

    @Test
    void fixedCoupon_returns_exact_amount() {
        DiscountPolicy policy = new FixedCouponDiscount(Money.of(1.00));
        Money subtotal = Money.of(10.00);

        Money discount = policy.discountOf(subtotal);

        assertEquals(Money.of(1.00), discount);
    }

    @Test
    void noDiscount_returns_zero() {
        DiscountPolicy policy = new NoDiscount();
        Money subtotal = Money.of(10.00);

        Money discount = policy.discountOf(subtotal);

        assertEquals(Money.zero(), discount);
    }
}
