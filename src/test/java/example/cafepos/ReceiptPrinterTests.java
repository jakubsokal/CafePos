package example.cafepos;

import org.example.cafepos.checkout.PricingService;
import org.example.cafepos.checkout.ReceiptPrinter;
import org.example.cafepos.checkout.discount.DiscountPolicy;
import org.example.cafepos.checkout.discount.FixedCouponDiscount;
import org.example.cafepos.checkout.tax.FixedRateTaxPolicy;
import org.example.cafepos.checkout.tax.TaxPolicy;
import org.example.cafepos.common.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReceiptPrinterTests {
    @Test
    public void print_receipt_with_discount(){
        ReceiptPrinter printer = new ReceiptPrinter();
        DiscountPolicy discountPolicy = new FixedCouponDiscount(Money.of(1.00));
        TaxPolicy taxPolicy = new FixedRateTaxPolicy(5);
        PricingService pricingService = new PricingService(discountPolicy, taxPolicy);
        Money subtotal = Money.of(11.00);

        PricingService.PricingResult list = pricingService.price(subtotal);

        String receipt = printer.format("Cappuccino", 2, list, 5);
        String expectedReceipt = """
                Order (Cappuccino) x2
                Subtotal: 11.00
                Discount: -1.00
                Tax (5%): 0.50
                Total: 10.50""";

        assertEquals(expectedReceipt, receipt);
    }

    @Test
    public void print_receipt_no_discount(){
        ReceiptPrinter printer = new ReceiptPrinter();
        DiscountPolicy discountPolicy = new FixedCouponDiscount(Money.of(0.00));
        TaxPolicy taxPolicy = new FixedRateTaxPolicy(5);
        PricingService pricingService = new PricingService(discountPolicy, taxPolicy);
        Money subtotal = Money.of(10.00);

        PricingService.PricingResult list = pricingService.price(subtotal);

        String receipt = printer.format("Cappuccino", 2, list, 5);
        String expectedReceipt = """
                Order (Cappuccino) x2
                Subtotal: 10.00
                Tax (5%): 0.50
                Total: 10.50""";

        assertEquals(expectedReceipt, receipt);
    }
}
