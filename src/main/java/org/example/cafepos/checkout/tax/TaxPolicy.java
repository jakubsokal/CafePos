package org.example.cafepos.checkout.tax;

import org.example.cafepos.common.Money;

public interface TaxPolicy {
    Money taxOn(Money amount);
}
