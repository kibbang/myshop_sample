package sample.myshop.admin.product.repository;

import sample.myshop.admin.product.domain.Product;

public interface ProductRepository {
    Long save(Product product);
}
