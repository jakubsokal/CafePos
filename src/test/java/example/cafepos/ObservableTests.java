package example.cafepos;

import org.example.cafepos.catalog.SimpleProduct;
import org.example.cafepos.common.Money;
import org.example.cafepos.domain.LineItem;
import org.example.cafepos.domain.Order;
import org.example.cafepos.domain.OrderIds;
import org.example.cafepos.domain.OrderObserver;
import org.example.cafepos.payment.CardPayment;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ObservableTests {
    private final Order order;

    LineItem BCK;

    List<String> events;

    //We create a new order for each test so we can test the events from scratch
    private ObservableTests()  {
        events = new ArrayList<>();
        BCK = new LineItem(new SimpleProduct("P-BCK", "Biscuit Cake",
                Money.of(5.50)), 1);
        order = new Order(OrderIds.next());
        order.register((o, evt) -> events.add(evt));
    }

    @Test
    public void RegisterNullEvent(){
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> order.register(null));
        assertEquals("Observer cannot be null", result.getMessage());
    }

    @Test
    public void RegisterSameTwiceEvent(){
        //Arrange
        List<String> testList = new ArrayList<>();
        OrderObserver observer = (o, evt) -> testList.add(evt);

        //Act
        Order order1 = new Order(OrderIds.next());
        order1.register(observer);
        order1.register(observer);
        order1.addItem(BCK);

        order.register((o, evt) -> events.add(evt));
        order.addItem(BCK);

        /*Since events.size() = 1 we can tell only one observer was added if we add two observers
        * events.size() = 2 we can see that in the second assert as order has one observer added in
        * the constructor and second in this test*/
        //Assert
        assertEquals(1, testList.size());
        assertTrue(testList.contains("itemAdded"));
        assertEquals(2, events.size());
        assertEquals(List.of("itemAdded", "itemAdded"), events);

    }

    @Test
    public void UnregisterEvent(){
        //Arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        String expected = "Observer removed successfully";
        OrderObserver observer = (o, evt) -> events.add(evt);

        //Act
        order.register(observer);
        order.unregister(observer);
        String output = outputStream.toString().trim();

        //Assert
        assertEquals(expected, output);
        assertEquals(0, events.size());
    }

    @Test
    public void UnregisterUnknowEvent(){
        //Arrange
        String expected = "Observer is not registered";

        //Act
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
                () ->  order.unregister((o, evt) -> events.add(evt)));

        //Assert
        assertEquals(expected, result.getMessage());
        assertEquals(0, events.size());
    }

    @Test
    public void KitchenDisplayAddedItemTest_Success() {
        //Arrange
        String expected = "itemAdded";

        //Act
        order.addItem(BCK);

        //Assert
        assertTrue(events.contains(expected));
        assertEquals(1, events.size());
    }

    @Test
    public void KitchenDisplayAddedMultipleItemsTest_Success() {
        //Arrange
        List<String> expected = List.of("itemAdded", "itemAdded");

        //Act
        order.addItem(new LineItem(new SimpleProduct("P-CFE", "Coffee",
                Money.of(3.50)), 1));
        order.addItem(BCK);

        //Assert
        assertEquals(events, expected);
        assertEquals(2, events.size());
    }

    @Test
    public void KitchenDisplayPaidTest_Success() {
        //Arrange
        String expected = "paid";

        //Act
        order.pay(new CardPayment("1234567890000000"));

        //Assert
        assertTrue(events.contains(expected));
        assertEquals(1, events.size());;
    }

    @Test
    public void CustomerNotifierReadyTest_Success() {
        //Arrange
        List<String> expected = List.of( "ready");

        //Act
        order.markReady();

        //Assert
        assertEquals(expected, events);
        assertEquals(1, events.size());
    }
}
