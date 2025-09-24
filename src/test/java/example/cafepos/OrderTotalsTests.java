package example.cafepos;

import org.example.cafepos.catalog.Catalog;
import org.example.cafepos.catalog.InMemoryCatalog;
import org.example.cafepos.catalog.SimpleProduct;
import org.example.cafepos.common.Money;
import org.example.cafepos.domain.LineItem;
import org.example.cafepos.domain.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderTotalsTests {
    @Test
    void order_totals() {
        var p1 = new SimpleProduct("A", "A", Money.of(2.50));
        var p2 = new SimpleProduct("B", "B", Money.of(3.50));
        var o = new Order(1);
        o.addItem(new LineItem(p1, 2));
        o.addItem(new LineItem(p2, 1));
        assertEquals(Money.of(8.50), o.subtotal());
        assertEquals(Money.of(0.85), o.taxAtPercent(10));
        assertEquals(Money.of(9.35), o.totalWithTax(10));
    }

    @Test
    void negativeValueProduct() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleProduct("A", "A", Money.of(-2.50));
        });
    }

    @Test
    void NegativeTax(){
        var p1 = new SimpleProduct("A", "A", Money.of(2.50));
        var o = new Order(1);
        o.addItem(new LineItem(p1, 2));
        assertThrows(IllegalArgumentException.class, () -> {
            o.taxAtPercent(-10);
        });
    }

    @Test
    void NoProductAdd(){
        var catalog = new InMemoryCatalog();
        assertThrows(IllegalArgumentException.class, () -> {
            catalog.add(null);
        });
    }
}
