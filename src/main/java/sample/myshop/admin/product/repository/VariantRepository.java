package sample.myshop.admin.product.repository;

import sample.myshop.admin.product.domain.Variant;

import java.util.List;

public interface VariantRepository {
    void save(Variant variant);

    List<Variant> findByProductId(Long productId);
}
