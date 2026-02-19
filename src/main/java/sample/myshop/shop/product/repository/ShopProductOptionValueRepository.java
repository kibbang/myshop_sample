package sample.myshop.shop.product.repository;

import sample.myshop.admin.product.domain.OptionValue;

import java.util.List;

public interface ShopProductOptionValueRepository {
    List<OptionValue> findByOptionId(List<Long> optionIds);
}
