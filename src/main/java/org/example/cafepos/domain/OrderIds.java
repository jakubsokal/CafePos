package org.example.cafepos.domain;

import java.util.concurrent.atomic.AtomicLong;

public class OrderIds {
    private static final AtomicLong nextId = new AtomicLong(1);

    public static long next() {
        return nextId.getAndIncrement();
    }
}
