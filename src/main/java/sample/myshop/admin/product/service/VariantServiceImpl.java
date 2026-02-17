package sample.myshop.admin.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.admin.product.domain.*;
import sample.myshop.admin.product.domain.dto.web.VariantCreateDto;
import sample.myshop.admin.product.domain.dto.web.VariantInfoDto;
import sample.myshop.admin.product.repository.*;
import sample.myshop.common.exception.BadRequestException;
import sample.myshop.common.exception.ProductNotFoundException;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VariantServiceImpl implements VariantService {

    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final InventoryRepository inventoryRepository;
    private final OptionRepository optionRepository;
    private final VariantOptionValueRepository variantOptionValueRepository;

    @Override
    @Transactional
    public Long createVariant(VariantCreateDto variantCreateDto, List<Long> normalizedOptionValueIds) {

        Product product = productRepository.findById(variantCreateDto.getProductId());

        if (product == null) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다.");
        }

        Variant variant = Variant.create(
                product,
                variantCreateDto.getSku(),
                variantCreateDto.getCustomPrice()
        );

        variantRepository.save(variant);

        Inventory newInitInventory = Inventory.createZero(variant);
        inventoryRepository.save(newInitInventory);

        List<OptionValue> foundOptionValues = optionRepository.findAllOptionValuesByIds(normalizedOptionValueIds);

        if (normalizedOptionValueIds.size() != foundOptionValues.size()) {
            throw new IllegalArgumentException("유효하지 않은 옵션값이 포함되어 있습니다.");
        }

        foundOptionValues.forEach(
                optionValue ->
                        variantOptionValueRepository.save(
                                VariantOptionValue.create(variant, optionValue)
                        )
        );

        return variant.getId();
    }

    @Override
    public List<Variant> getVariants(Long productId) {
        return variantRepository.findByProductId(productId);
    }

    @Override
    public List<VariantOptionValue> getVariantOptionValuesByIds(List<Long> variantIds) {
        return variantOptionValueRepository.findOptionJoinsByVariantIds(variantIds);
    }

    @Override
    public List<VariantInfoDto> getVariantInfoList(List<Variant> variants, List<VariantOptionValue> variantOptionValueList) {
        Map<Long, List<VariantOptionValue>> grouped = variantOptionValueList.stream()
                .collect(groupingBy(
                        variantOptionValue -> variantOptionValue.getVariant().getId()
                ));

        return variants.stream()
                .map(variant -> {
                    int quantity = variant.getInventory() != null ? variant.getInventory().getStockQuantity() : 0;
                    int price = variant.getPrice() == null ? variant.getProduct().getBasePrice() : variant.getPrice();

                    String optionSummary = getOptionSummary(variant, grouped);

                    return new VariantInfoDto(
                            variant.getId(),
                            variant.isDefault(),
                            variant.getSku(),
                            price,
                            quantity,
                            optionSummary
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public void changeVariantStock(Long productId, Long variantId, int stockQuantity) {
        if (stockQuantity < 0) {
            throw new BadRequestException("재고는 0 이상이여야합니다");
        }

        Inventory inventory = inventoryRepository.findForUpdateByProductIdAndVariantId(productId, variantId);
        inventory.updateStockQuantity(stockQuantity);
    }

    private String getOptionSummary(Variant variant, Map<Long, List<VariantOptionValue>> grouped) {
        String optionSummary;

        if (variant.isDefault()) {
            optionSummary = "기본";
        } else {
            List<VariantOptionValue> list = grouped.getOrDefault(variant.getId(), List.of());
            optionSummary = list.stream().map(variantOptionValue -> {
                OptionValue optionValue = variantOptionValue.getOptionValue();
                String optionName = optionValue.getOption().getName();
                String value = optionValue.getValue();

                return optionName + " : " + value;
            }).collect(joining(", "));

            if (optionSummary.isBlank()) {
                optionSummary = "-";
            }
        }
        return optionSummary;
    }
}
