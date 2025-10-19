package org.example.cafepos.catalog;
import org.example.cafepos.common.Money;
import org.example.cafepos.common.Product;
import org.example.cafepos.common.Priced;

public final class SimpleProduct implements Product,Priced {
    private final String id;

    private final String name;

    private final Money basePrice;

    public SimpleProduct(String id, String name, Money basePrice) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
    }

    @Override public String id() {
        return id;
    }

    @Override public String name() {
        return name;
    }

    @Override public Money basePrice() {
        return basePrice;
    }

    //had to implement Priced to overide this method
    @Override
    public Money price() {
        return basePrice();  // Simple product price is just its base price
    }

}
