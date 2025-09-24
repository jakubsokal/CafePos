package org.example.cafepos.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money implements Comparable<Money> {
    private final BigDecimal amount;
    public static Money of(double value) {
        return new Money(BigDecimal.valueOf(value));
    }
    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    private Money(BigDecimal a) {
        if (a == null || a.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount must be non-negative and not null");
        }

        this.amount = a.setScale(2, RoundingMode.HALF_UP);
    }

    public Money add(Money other) {
        if(other.amount.compareTo(BigDecimal.ZERO) < 0 ) throw new IllegalArgumentException("amount must be non-negative");

        return new Money(this.amount.add(other.amount).setScale(2, RoundingMode.HALF_UP));
    }

    public Money multiply(int qty) {
        if (qty < 0) throw new IllegalArgumentException("quantity must be non-negative");

        return new Money(this.amount.multiply(BigDecimal.valueOf(qty)).setScale(2, RoundingMode.HALF_UP));
    }

    @Override
    public int compareTo(Money other) {
        if (other == null) throw new IllegalArgumentException("Other Money object cannot be null");

        return this.amount.compareTo(other.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;

        return amount.equals(money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}
