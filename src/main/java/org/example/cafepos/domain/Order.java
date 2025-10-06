package org.example.cafepos.domain;

import org.example.cafepos.common.Money;
import org.example.cafepos.payment.PaymentStrategy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public final class Order {
    private final long id;

    private final List<OrderObserver> observers = new ArrayList<>();

    private final List<LineItem> items = new ArrayList<>();

    public Order(long id) {
        this.id = id;
    }

    public void addItem(LineItem li) {
        items.add(li);
        notifyObservers("itemAdded");
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
        notifyObservers("paid");
    }

    public long id() {
        return id;
    }

    public List<LineItem> items() {
        return items;
    }

    public List<OrderObserver> getObservers(){
        return observers;
    }

    public void register(OrderObserver o) {
    // if an observer does not exist it adds to the list of observers
        if (o == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    public void unregister(OrderObserver o) {
    // if the observer exists,it deletes it else it throws an error
        if (observers.contains(o)) {
            observers.remove(o);
            System.out.println("Observer removed successfully");
        } else {
            throw new IllegalArgumentException("Observer is not registered");
        }
    }

    // 2) Publish events
    private void notifyObservers(String eventType) {
    // scans the list of observers for the observer and updates it with the eventType
        for (OrderObserver o : observers) {
            o.updated(this, eventType);
        }
    }

    public void markReady() {
        notifyObservers("ready");
    }
}