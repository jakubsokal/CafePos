package org.example.cafepos.decorator;

import org.example.cafepos.common.Money;
import org.example.cafepos.common.Priced;
import org.example.cafepos.common.Product;

public final class Syrup extends ProductDecorator {
            private static final Money SURCHARGE = Money.of(0.40);

            public Syrup(Product base) { super(base); }
            @Override public String name() { return base.name() + " + Syrup"; } 

    public Money price() { return (base instanceof Priced p ? p.price() : base.basePrice()).add(SURCHARGE); }
            }
