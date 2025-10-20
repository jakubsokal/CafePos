package example.cafepos;

import org.example.cafepos.checkout.PricingService;
import org.example.cafepos.checkout.ReceiptPrinter;
import org.example.cafepos.checkout.service.CheckoutService;
import org.example.cafepos.checkout.tax.FixedRateTaxPolicy;
import org.example.cafepos.checkout.discount.NoDiscount;
import org.example.cafepos.factory.ProductFactory;
import org.example.cafepos.payment.PaymentStrategy;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckoutServicePaymentStrategyTest {
    @Test
    void calls_injected_payment_strategy() {
        var pricing = new PricingService(new NoDiscount(), new FixedRateTaxPolicy(10));
        var printer = new ReceiptPrinter();
        var checkout = new CheckoutService(new ProductFactory(), pricing, printer, 10);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        PaymentStrategy strategy = order -> System.out.println("[TestStrategy] Paid " + order.totalWithTax(10));
        checkout.checkout("ESP+SHOT", 1, strategy);

        String output = out.toString();
        assertTrue(output.contains("[TestStrategy] Paid"));
    }
}
