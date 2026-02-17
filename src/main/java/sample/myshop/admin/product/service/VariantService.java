package sample.myshop.admin.product.service;

import sample.myshop.admin.product.domain.Variant;
import sample.myshop.admin.product.domain.dto.web.VariantCreateDto;

import java.util.List;

public interface VariantService {
    Long createVariant(VariantCreateDto variantCreateDto, List<Long> normalizedOptionValueIds);
    void getVariants(Long productId);
}
