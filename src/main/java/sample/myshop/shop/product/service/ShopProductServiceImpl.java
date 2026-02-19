package sample.myshop.shop.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.admin.product.domain.Option;
import sample.myshop.admin.product.domain.OptionValue;
import sample.myshop.admin.product.domain.Variant;
import sample.myshop.admin.product.repository.OptionRepository;
import sample.myshop.shop.product.api.dto.ProductVariantMatchResponse;
import sample.myshop.shop.product.domain.dto.web.ShopProductDetailDto;
import sample.myshop.shop.product.domain.dto.web.ShopProductListItemDto;
import sample.myshop.shop.product.domain.dto.web.ShopProductOptionDto;
import sample.myshop.shop.product.domain.dto.web.ShopProductOptionValueDto;
import sample.myshop.shop.product.repository.*;


import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShopProductServiceImpl implements ShopProductService {

    private final ShopProductRepository shopProductRepository;
    private final ShopVariantRepository shopVariantRepository;
    private final ShopInventoryRepository shopInventoryRepository;
    private final ShopProductOptionRepository shopProductOptionRepository;
    private final ShopProductOptionValueRepository shopProductOptionValueRepository;

    @Override
    public List<ShopProductListItemDto> getList() {
        return shopProductRepository.findAll();
    }

    @Override
    public ShopProductDetailDto getDetail(Long productId) {
        return shopProductRepository.findById(productId);
    }

    @Override
    public ProductVariantMatchResponse getMatchedVariant(Long productId, List<Long> optionValueIds) {
        Long matchedVariantId = shopProductRepository.findMatchedVariant(productId, optionValueIds);

        if (matchedVariantId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Variant matchedVariant = shopVariantRepository.findById(matchedVariantId);

        if (matchedVariant == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Inventory inventory = shopInventoryRepository.findByVariantId(matchedVariantId);

        return new ProductVariantMatchResponse(
                true,
                matchedVariantId,
                matchedVariant.getSku(),
                inventory.getStockQuantity(),
                inventory.getStockQuantity() > 0
        );
    }

    @Override
    public List<ShopProductOptionDto> getOptionsWithValuesByProductId(Long productId) {
        List<Option> productOptions = shopProductOptionRepository.findByProductId(productId);

        List<Long> distinctOptionIds = productOptions.stream().map(option -> option.getId()).distinct().toList();

        List<OptionValue> optionValueList = shopProductOptionValueRepository.findByOptionId(distinctOptionIds);

        Map<Long, List<OptionValue>> optionValuesByOptionId = optionValueList.stream().collect(Collectors.groupingBy(optionValue -> optionValue.getOption().getId()));

        return productOptions.stream()
                .map(option -> {
                    List<OptionValue> optionValues = optionValuesByOptionId.getOrDefault(option.getId(), List.of());

                    List<ShopProductOptionValueDto> shopProductOptionValues = optionValues.stream().sorted(Comparator.comparingInt(OptionValue::getSortOrder))
                            .map(optionValue -> new ShopProductOptionValueDto(optionValue.getId(), optionValue.getValue()))
                            .toList();

                    return new ShopProductOptionDto(option.getId(), option.getName(), shopProductOptionValues);
                })
                .toList();

    }

    public Long getDefaultVariant(Long productId) {
        return shopVariantRepository.findDefaultVariantIdByProductId(productId);
    }
}
