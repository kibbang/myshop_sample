package sample.myshop.shop.product.repository;

import sample.myshop.shop.product.domain.dto.web.ShopProductDetailDto;
import sample.myshop.shop.product.domain.dto.web.ShopProductListItemDto;

import java.util.List;

public interface ShopProductRepository {
    List<ShopProductListItemDto> findAll();

    ShopProductDetailDto findById(Long productId);
}
