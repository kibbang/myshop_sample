package sample.myshop.shop.product.service;

import sample.myshop.shop.product.domain.dto.web.ShopProductDetailDto;
import sample.myshop.shop.product.domain.dto.web.ShopProductListItemDto;

import java.util.List;

public interface ShopProductService {
    List<ShopProductListItemDto> getList();
    ShopProductDetailDto getDetail(Long productId);
}
