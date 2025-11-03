package example.cafepos;

import org.example.cafepos.printing.LegacyPrinterAdapter;
import org.example.cafepos.printing.Printer;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class FakeLegacy extends vendor.legacy.LegacyThermalPrinter {
    int lastLen = -1;

    @Override
    public void legacyPrint(byte[] payload) {
        lastLen = payload.length;
    }

    @Test
    void adapter_converts_text_to_bytes() {
        var fake = new FakeLegacy();
        Printer p = new LegacyPrinterAdapter(fake);
        p.print("ABC");
        assertTrue(fake.lastLen >= 3);
    }

    @Test
    void adapter_handles_empty_string() {
        var fake = new FakeLegacy();
        Printer p = new LegacyPrinterAdapter(fake);
        p.print("");
        assertEquals(0, fake.lastLen);
    }

    @Test
    void adapter_handles_unicode_correctly() {
        var fake = new FakeLegacy();
        Printer p = new LegacyPrinterAdapter(fake);
        p.print("€");
        assertEquals(3, fake.lastLen);
    }

    @Test
    void adapter_handles_realistic_receipt() {
        var fake = new FakeLegacy();
        Printer p = new LegacyPrinterAdapter(fake);
        String receipt = "Order (LAT+L) x2\nSubtotal: 7.80\nTax (10%): 0.78\nTotal: 8.58";;
        p.print(receipt);
        assertEquals(receipt.getBytes(StandardCharsets.UTF_8).length, fake.lastLen);
    }

    @Test
    void adapter_throws_on_null_text() {
        var fake = new FakeLegacy();
        Printer p = new LegacyPrinterAdapter(fake);
        assertThrows(NullPointerException.class, () -> p.print(null));
    }
}