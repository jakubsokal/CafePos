package example.cafepos;

import org.example.cafepos.checkout.tax.FixedRateTaxPolicy;
import org.example.cafepos.checkout.tax.TaxPolicy;
import org.example.cafepos.common.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaxPolicyTest {
    @Test
    public void fixed_rate_tax_calculation() {
        TaxPolicy policy = new FixedRateTaxPolicy(5);

        Money amount = Money.of(100.00);
        Money tax = policy.taxOn(amount);

        assertEquals(Money.of(5.00), tax);
    }

    @Test
    public void fixed_rate_tax_get_percent() {
        FixedRateTaxPolicy policy = new FixedRateTaxPolicy(7);

        int percent = policy.getPercent();

        assertEquals(7, percent);
    }
}
