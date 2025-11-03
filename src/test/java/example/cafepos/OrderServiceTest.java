package example.cafepos;

import org.example.cafepos.common.Money;
import org.example.cafepos.domain.Order;
import org.example.cafepos.domain.OrderIds;
import org.example.cafepos.order.OrderService;
import org.example.cafepos.payment.PaymentStrategy;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


public class OrderServiceTest {

    @Test
    void addItemAddsCorrectly() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);

        service.addItem("ESP+OAT+SHOT+SYP+L", 1);

        assertEquals(1, order.items().size());
        assertEquals(1, order.items().get(0).quantity());
        assertEquals("Espresso + Oat Milk + Extra Shot + Syrup (Large)", order.items().get(0).product().name());
        assertEquals(Money.of(4.90), order.items().get(0).lineTotal());
    }

    @Test
    void removeLastItemRemovesLast() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);

        service.addItem("ESP+OAT+SHOT+SYP+L", 1);
        service.removeLastItem();

        assertEquals(0, order.items().size());
    }

    @Test
    void totalWithTaxReturnsPositive() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);

        service.addItem("ESP+OAT+SHOT+SYP+L", 1);
        Money total = service.totalWithTax(10);

        assertNotNull(total);
        assertTrue(total.asBigDecimal().compareTo(Money.zero().asBigDecimal()) > 0);
    }

    @Test
    void payProcessesWithoutException() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);

        service.addItem("ESP+OAT+SHOT+SYP+L", 1);

        PaymentStrategy strategy = (o) -> {
            // no-op payment strategy for test
        };

        assertDoesNotThrow(() -> service.pay(strategy, 10));
    }
}