package sample.myshop.shop.product.service;

import sample.myshop.shop.product.api.dto.ProductVariantMatchResponse;
import sample.myshop.shop.product.domain.dto.web.ShopProductDetailDto;
import sample.myshop.shop.product.domain.dto.web.ShopProductListItemDto;
import sample.myshop.shop.product.domain.dto.web.ShopProductOptionDto;

import java.util.List;

public interface ShopProductService {
    List<ShopProductListItemDto> getList();
    ShopProductDetailDto getDetail(Long productId);

    ProductVariantMatchResponse getMatchedVariant(Long productId, List<Long> optionValueIds);

    List<ShopProductOptionDto> getOptionsWithValuesByProductId(Long productId);

    Long getDefaultVariant(Long productId);
}
