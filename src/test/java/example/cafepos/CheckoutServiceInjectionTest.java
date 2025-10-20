package example.cafepos;

import org.example.cafepos.checkout.PricingService;
import org.example.cafepos.checkout.ReceiptPrinter;
import org.example.cafepos.checkout.service.CheckoutService;
import org.example.cafepos.checkout.tax.FixedRateTaxPolicy;
import org.example.cafepos.checkout.discount.LoyaltyPercentDiscount;
import org.example.cafepos.factory.ProductFactory;
import org.example.cafepos.payment.PaymentStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckoutServiceInjectionTest {
    @Test
    void uses_injected_services_correctly() {
        var pricing = new PricingService(new LoyaltyPercentDiscount(5), new FixedRateTaxPolicy(10));
        var printer = new ReceiptPrinter();
        var checkout = new CheckoutService(new ProductFactory(), pricing, printer, 10);

        PaymentStrategy dummy = order -> {};

        String receipt = checkout.checkout("LAT+L", 2, dummy);

        assertTrue(receipt.contains("Subtotal: 7.80"));
        assertTrue(receipt.contains("Discount: -0.39"));
        assertTrue(receipt.contains("Tax (10%): 0.74"));
        assertTrue(receipt.contains("Total: 8.15"));


    }
}

