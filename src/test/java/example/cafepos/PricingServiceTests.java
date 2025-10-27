package example.cafepos;

import org.example.cafepos.checkout.PricingService;
import org.example.cafepos.checkout.discount.DiscountPolicy;
import org.example.cafepos.checkout.discount.FixedCouponDiscount;
import org.example.cafepos.checkout.tax.FixedRateTaxPolicy;
import org.example.cafepos.checkout.tax.TaxPolicy;
import org.example.cafepos.common.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PricingServiceTests {
    @Test
    public void pricingService_calculates_total_price() {
        DiscountPolicy discountPolicy = new FixedCouponDiscount(Money.of(1.00));
        TaxPolicy taxPolicy = new FixedRateTaxPolicy(5);
        PricingService pricingService = new PricingService(discountPolicy, taxPolicy);
        Money subtotal = Money.of(11.00);

        PricingService.PricingResult list = pricingService.price(subtotal);

        assertEquals(Money.of(11.00), list.subtotal());
        assertEquals(Money.of(1.00), list.discount());
        assertEquals(Money.of(0.50), list.tax());
        assertEquals(Money.of(10.50), list.total());
    }

    @Test
    public void pricingService_calculates_total_price_tax_100() {
        DiscountPolicy discountPolicy = new FixedCouponDiscount(Money.of(11.00));
        TaxPolicy taxPolicy = new FixedRateTaxPolicy(5);
        PricingService pricingService = new PricingService(discountPolicy, taxPolicy);
        Money subtotal = Money.of(11.00);

        PricingService.PricingResult list = pricingService.price(subtotal);

        assertEquals(Money.of(11.00), list.subtotal());
        assertEquals(Money.of(11.00), list.discount());
        assertEquals(Money.of(0.00), list.tax());
        assertEquals(Money.of(0.00), list.total());
    }
}
