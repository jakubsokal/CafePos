package org.example.cafepos.domain;

import org.example.cafepos.common.Money;
import org.example.cafepos.payment.PaymentStrategy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public final class Order {
    private final long id;

    private final List<LineItem> items = new ArrayList<>();

    public Order(long id) {
        this.id = id;
    }

    public void addItem(LineItem li) {
        items.add(li);
    }

    public Money subtotal() {
        return items.stream().map(LineItem::lineTotal)
                .reduce(Money.zero(), Money::add);
    }

    public Money taxAtPercent(int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException(
                    "Your value must be non-negative and less than or equal to 100"
            );
        }

        BigDecimal subtotalAmount = new BigDecimal(subtotal().toString());
        BigDecimal taxAmount = subtotalAmount.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return Money.of(taxAmount.doubleValue());
    }

    public Money totalWithTax(int percent) {
        return subtotal().add(taxAtPercent(percent));
    }

    public void pay(PaymentStrategy payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Strategy required");
        }

        payment.pay(this);
    }

    public long id() {
        return id;
    }

    public List<LineItem> items() {
        return items;
    }
}