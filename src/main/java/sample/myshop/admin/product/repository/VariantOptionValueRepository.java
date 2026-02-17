package sample.myshop.admin.product.repository;

import sample.myshop.admin.product.domain.VariantOptionValue;

import java.util.List;

public interface VariantOptionValueRepository {
    void save(VariantOptionValue variantOptionValue);
    List<VariantOptionValue> findOptionJoinsByVariantIds(List<Long> variantIds);
}
