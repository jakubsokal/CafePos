package example.cafepos;

import org.example.cafepos.catalog.SimpleProduct;
import org.example.cafepos.common.Money;
import org.example.cafepos.domain.LineItem;
import org.example.cafepos.domain.Order;
import org.example.cafepos.payment.CardPayment;
import org.example.cafepos.payment.CashPayment;
import org.example.cafepos.payment.PaymentStrategy;
import org.example.cafepos.payment.WalletPayment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentStrategyTest {

    private final Order order;

    public PaymentStrategyTest() {
        SimpleProduct product = new SimpleProduct("A", "Product A", Money.of(10));
        order = new Order(1);
        order.addItem(new LineItem(product, 2));
    }

    @Test
    void payment_strategy_called() {
        //Arrange
        final boolean[] called = {false};
        PaymentStrategy fake = o -> called[0] = true;

        //Act
        order.pay(fake);

        //Assert
        assertTrue(called[0], "Payment strategy should be called");
    }

    @Test
    void PaymentFailsWhenStrategyIsNull() {
        //Arrange
        String actualMessage = "Strategy required";

        //Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> order.pay(null));
        assertEquals(actualMessage, exception.getMessage());
    }

    @Test
    void CashPaymentSuccess() {
        //Arrange
        CashPayment cashPayment = new CashPayment();

        //Act
        order.pay(cashPayment);

        //Assert
        assertEquals(Money.of(22), order.totalWithTax(10));
    }

    @Test
    void CardPaymentSuccess() {
        //Arrange
        CardPayment cardPayment = new CardPayment("1234567890000000");

        //Act
        order.pay(cardPayment);

        //Assert
        assertEquals(Money.of(22), order.totalWithTax(10));
    }

    @Test
    void CardPaymentTooShortCardNumber() {
        //Arrange
        CardPayment cardPayment = new CardPayment("123");
        String actualMessage = "Card number must be 16 digits long";

        //Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> order.pay(cardPayment));
        assertEquals(actualMessage, exception.getMessage());
    }

    @Test
    void CardPaymentInvalidCardNumber() {
        //Arrange
        CardPayment cardPayment = new CardPayment("ABC1234567890000");
        String actualMessage = "Card number must contain only digits";

        //Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> order.pay(cardPayment));
        assertEquals(actualMessage, exception.getMessage());
    }

    @Test
    void WalletPaymentSuccess() {
        //Arrange
        WalletPayment cardPayment = new WalletPayment("alice-wallet-01");

        //Act
        order.pay(cardPayment);

        //Assert
        assertEquals(Money.of(22), order.totalWithTax(10));
    }
}