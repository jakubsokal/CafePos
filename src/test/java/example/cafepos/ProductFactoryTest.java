package example.cafepos;

import org.example.cafepos.common.Money;
import org.example.cafepos.domain.LineItem;
import org.example.cafepos.domain.Order;
import org.example.cafepos.domain.OrderIds;
import org.example.cafepos.factory.ProductFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductFactoryTest {
    private final ProductFactory productFactory;
    private final Order order;

    public ProductFactoryTest(){
        productFactory = new ProductFactory();
        order = new Order(OrderIds.next());
    }

    @Test
    public void testCreateProduct(){
        String productString = "ESP+OAT+SHOT+SYP+L";
        var product = productFactory.create(productString);
        order.addItem(new LineItem(product, 1));
        assertEquals( 1, order.items().size());
        assertEquals("Espresso + Oat Milk + Extra Shot + Syrup (Large)", order.items().getFirst().product().name());
        assertEquals(Money.of(4.90), order.items().getFirst().lineTotal());
    }

    @Test
    public void testCreateProductIsNULL(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productFactory.create(null));
        assertEquals( 0, order.items().size());
        assertEquals("recipe required", exception.getMessage());
    }

    @Test
    public void testCreateProductIsEmpty(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productFactory.create(""));
        assertEquals( 0, order.items().size());
        assertEquals("recipe required", exception.getMessage());
    }

    @Test
    public void testCreateProductInvalidProduct(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productFactory.create("NOTHING"));
        assertEquals( 0, order.items().size());
        assertEquals("Unknown base: NOTHING", exception.getMessage());
    }

    @Test
    public void testCreateProductInvalidAddOn(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productFactory.create("LAT+NOTHING"));
        assertEquals( 0, order.items().size());
        assertEquals("Unknown addon: NOTHING", exception.getMessage());
    }
}
