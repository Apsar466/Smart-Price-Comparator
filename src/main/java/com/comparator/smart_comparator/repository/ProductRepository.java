package com.comparator.smart_comparator.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.comparator.smart_comparator.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameIgnoreCase(String name);

    List<Product> findByNameIgnoreCaseOrderByPriceAsc(String name);

    List<Product> findByPriceBetween(double min, double max);

    List<Product> findByNameIgnoreCaseAndStoreNameIgnoreCase(String name, String storeName);

    List<Product> findByNameIgnoreCaseAndStoreNameIgnoreCaseAndPriceBetween(
            String name, String storeName, double min, double max);
}