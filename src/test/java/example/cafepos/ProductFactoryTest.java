package example.cafepos;

import org.example.cafepos.catalog.SimpleProduct;
import org.example.cafepos.common.Money;
import org.example.cafepos.common.Priced;
import org.example.cafepos.common.Product;
import org.example.cafepos.decorator.ExtraShot;
import org.example.cafepos.decorator.OatMilk;
import org.example.cafepos.decorator.SizeLarge;
import org.example.cafepos.domain.LineItem;
import org.example.cafepos.domain.Order;
import org.example.cafepos.domain.OrderIds;
import org.example.cafepos.factory.ProductFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test void decorator_single_addon() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso",
                Money.of(2.50));
        Product withShot = new ExtraShot(espresso);
        assertEquals("Espresso + Extra Shot", withShot.name());
// if using Priced interface:
        assertEquals(Money.of(3.30), ((Priced) withShot).price());
    }

    @Test void decorator_stacks() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso",
                Money.of(2.50));
        Product decorated = new SizeLarge(new OatMilk(new
                ExtraShot(espresso)));
        assertEquals("Espresso + Extra Shot + Oat Milk (Large)",
                decorated.name());
        assertEquals(Money.of(4.50), ((Priced) decorated).price());
    }

    @Test void factory_parses_recipe() {
        ProductFactory f = new ProductFactory();
        Product p = f.create("ESP+SHOT+OAT");
        assertTrue(p.name().contains("Espresso") &&
                p.name().contains("Oat Milk"));
    }

    @Test void order_uses_decorated_price() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso",
                Money.of(2.50));
        Product withShot = new ExtraShot(espresso); // 3.30
        Order o = new Order(1);
        o.addItem(new LineItem(withShot, 2));
        assertEquals(Money.of(6.60), o.subtotal());
    }
}
