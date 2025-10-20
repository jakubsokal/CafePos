package example.cafepos;

import org.example.cafepos.checkout.PricingService;
import org.example.cafepos.checkout.ReceiptPrinter;
import org.example.cafepos.checkout.service.CheckoutService;
import org.example.cafepos.checkout.tax.FixedRateTaxPolicy;
import org.example.cafepos.checkout.discount.NoDiscount;
import org.example.cafepos.factory.ProductFactory;
import org.example.cafepos.payment.PaymentStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckoutServiceGlobalStateTest {
    @Test
    void uses_local_tax_percent_not_global() {
        var pricing = new PricingService(new NoDiscount(), new FixedRateTaxPolicy(7)); // 7% tax
        var printer = new ReceiptPrinter();
        var checkout = new CheckoutService(new ProductFactory(), pricing, printer, 7);

        PaymentStrategy dummy = order -> {};

        String receipt = checkout.checkout("ESP", 1, dummy);

        assertTrue(receipt.contains("Tax (7%)"));
    }
}

