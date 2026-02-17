package sample.myshop.admin.product.service;

import sample.myshop.admin.product.domain.Variant;
import sample.myshop.admin.product.domain.VariantOptionValue;
import sample.myshop.admin.product.domain.dto.web.VariantCreateDto;
import sample.myshop.admin.product.domain.dto.web.VariantInfoDto;

import java.util.List;

public interface VariantService {
    Long createVariant(VariantCreateDto variantCreateDto, List<Long> normalizedOptionValueIds);
    List<Variant> getVariants(Long productId);
    List<VariantOptionValue> getVariantOptionValuesByIds(List<Long> variantIds);

    List<VariantInfoDto> getVariantInfoList(List<Variant> variants, List<VariantOptionValue> variantOptionValueList);
}
