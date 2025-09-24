package org.example.cafepos.catalog;
import org.example.cafepos.common.Product;
import java.util.Optional;

public interface Catalog {
    void add(Product p);
    Optional<Product> findById(String id);
}