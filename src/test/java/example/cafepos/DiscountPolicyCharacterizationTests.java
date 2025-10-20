package example.cafepos;

import org.example.cafepos.smells.OrderManagerGod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DiscountPolicyCharacterizationTests {
    @Test
    void loyal5_discount() {
        String receipt = OrderManagerGod.process("LAT+L", 2, "CARD", "LOYAL5", false);
        assertTrue(receipt.contains("Discount: -0.39"));
        assertTrue(receipt.contains("Total: 8.15"));
    }

    @Test
    void coupon1_discount() {
        String receipt = OrderManagerGod.process("ESP", 1, "CASH", "COUPON1", false);
        assertTrue(receipt.contains("Discount: -1.00"));
        assertTrue(receipt.contains("Total: 1.65"));
    }

    @Test
    void no_discount() {
        String receipt = OrderManagerGod.process("LAT", 1, "CARD", "NONE", false);
        assertFalse(receipt.contains("Discount:"));
        assertTrue(receipt.contains("Total: 3.52"));
    }

    @Test
    void unknown_discount_code() {
        String receipt = OrderManagerGod.process("LAT", 1, "WALLET", "INVALID", false);
        assertFalse(receipt.contains("Discount:"));
        assertTrue(receipt.contains("Total: 3.52"));
    }
}