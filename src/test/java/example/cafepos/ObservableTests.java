package example.cafepos;

import org.example.cafepos.catalog.SimpleProduct;
import org.example.cafepos.common.Money;
import org.example.cafepos.domain.LineItem;
import org.example.cafepos.domain.Order;
import org.example.cafepos.domain.OrderIds;
import org.example.cafepos.domain.observers.CustomerNotifier;
import org.example.cafepos.domain.observers.DeliveryDesk;
import org.example.cafepos.domain.observers.KitchenDisplay;
import org.example.cafepos.payment.CardPayment;
import org.example.cafepos.payment.WalletPayment;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ObservableTests {
    private final Order order;

    SimpleProduct BCK;

    ByteArrayOutputStream outputStream;

    private ObservableTests()  {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        BCK = new SimpleProduct("P-BCK", "Biscuit Cake",
                Money.of(5.50));
        order = new Order(OrderIds.next());
        order.addItem(new LineItem(BCK, 2));
    }

    @Test
    public void RegisterNullEvent(){
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> order.register(null));
        assertEquals("Observer cannot be null", result.getMessage());
    }

    @Test
    public void RegisterSameTwiceEvent(){
        KitchenDisplay event = new KitchenDisplay();
        order.register(event);
        order.register(event);

        assertEquals(1, order.getObservers().size());
        assertEquals(event, order.getObservers().getFirst());
    }

    @Test
    public void UnregisterEvent(){
        //Arrange
        String expected = "Observer removed successfully\r\n";

        //Act
        KitchenDisplay event = new KitchenDisplay();
        order.register(event);
        order.unregister(event);
        String output = outputStream.toString();

        //Assert
        assertEquals(expected, output);
    }

    @Test
    public void UnregisterUnknowEvent(){
        //Arrange
        String expected = "Observer is not registered";

        //Act
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->  order.unregister(new KitchenDisplay()));

        //Assert
        assertEquals(expected, result.getMessage());
    }

    @Test
    public void KitchenDisplayAddedItemTest_Success() {
        //Arrange
        String expected = String.format("[Kitchen] Order #%d 1x Coffee added.\r\n", order.id());

        //Act
        order.register(new KitchenDisplay());
        order.addItem(new LineItem(new SimpleProduct("P-CFE", "Coffee",
                Money.of(3.50)), 1));
        String output = outputStream.toString();

        //Assert
        assertEquals(expected, output);
        assertEquals(2, order.items().size());
    }

    @Test
    public void KitchenDisplayAddedMultipleItemsTest_Success() {
        //Arrange
        String expected = String.format("""
                [Kitchen] Order #%d 1x Coffee added.\r
                [Kitchen] Order #%d 3x Biscuit Cake added.\r
                """, order.id(), order.id());

        //Act
        order.register(new KitchenDisplay());
        order.addItem(new LineItem(new SimpleProduct("P-CFE", "Coffee",
                Money.of(3.50)), 1));
        order.addItem(new LineItem(BCK, 3));
        String output = outputStream.toString();

        //Assert
        assertEquals(expected, output);
        assertEquals(3, order.items().size());
    }

    @Test
    public void KitchenDisplayPaidTest_Success() {
        //Arrange
        String expected = String.format("""
                [Card] Customer paid 12.10 EUR with card ****7890\r
                [Kitchen] Order #%d: Payment received.\r
                """, order.id());
        //Act
        order.register(new KitchenDisplay());
        order.pay(new CardPayment("1234567890"));
        String output = outputStream.toString();

        //Assert
        assertEquals(expected, output);
        assertEquals(Money.of(12.10), order.totalWithTax(10));
        assertEquals(1, order.items().size());
    }

    @Test
    public void CustomerNotifierAddedItemTest_Success() {
        //Arrange
        String expected = String.format("""
                [Kitchen] Order #%d 3x Biscuit Cake added.\r
                [Customer] Dear customer, your Order #%d has been updated: itemAdded.\r
                """, order.id(), order.id());

        //Act
        order.register(new KitchenDisplay());
        order.register(new CustomerNotifier());
        order.addItem(new LineItem(BCK, 3));
        String output = outputStream.toString();

        //Assert
        assertEquals(expected, output);
        assertEquals(2, order.items().size());
    }

    @Test
    public void CustomerNotifierReadyTest_Success() {
        //Arrange
        String walletId = "sinead-wallet-01";
        String expected = String.format("""
                [Wallet] Customer paid 12.10 EUR via wallet %s\r
                [Kitchen] Order #%d: Payment received.\r
                [Customer] Dear customer, your Order #%d has been updated: paid.\r
                [Customer] Dear customer, your Order #%d has been updated: ready.\r
                [Delivery] Order #%d is ready for delivery.\r
                """, walletId, order.id(), order.id(), order.id(), order.id());

        //Act
        order.register(new KitchenDisplay());
        order.register(new CustomerNotifier());
        order.register(new DeliveryDesk());
        order.pay(new WalletPayment(walletId));
        order.markReady();
        String output = outputStream.toString();

        //Assert
        assertEquals(expected, output);
        assertEquals(Money.of(12.10), order.totalWithTax(10));
    }
}
