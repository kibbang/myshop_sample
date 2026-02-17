package sample.myshop.admin.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.admin.product.domain.*;
import sample.myshop.admin.product.domain.dto.web.VariantCreateDto;
import sample.myshop.admin.product.domain.dto.web.VariantListDto;
import sample.myshop.admin.product.repository.*;
import sample.myshop.common.exception.ProductNotFoundException;

import java.util.List;

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
    public void getVariants(Long productId) {
        List<Variant> productVariants = variantRepository.findByProductId(productId);

//        productVariants.stream().map(variant -> new VariantListDto(
//                variant.getId(),
//                variant.getSku(),
//                variant.getPrice(),
//                variant.
//        ))
    }
}
