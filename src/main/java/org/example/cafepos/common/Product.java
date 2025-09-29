package org.example.cafepos.common;

public interface Product {
    String id();

    String name();

    Money basePrice();
}