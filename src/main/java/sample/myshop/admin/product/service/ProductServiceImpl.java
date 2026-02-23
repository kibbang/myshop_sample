package sample.myshop.admin.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.admin.product.domain.Product;
import sample.myshop.admin.product.domain.dto.web.*;
import sample.myshop.admin.product.repository.ProductRepository;
import sample.myshop.common.exception.ProductNotFoundException;
import sample.myshop.utils.ImageStorage;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ImageStorage imageStorage;

    @Override
    @Transactional
    public Long createProduct(ProductCreateDto productCreateDto, MultipartFile[] imageFiles) {
        Product product = Product.createProduct(
                productCreateDto.getCode(),
                productCreateDto.getName(),
                productCreateDto.getSlug(),
                productCreateDto.getDescription(),
                productCreateDto.getStatus(),
                productCreateDto.getBasePrice(),
                productCreateDto.getCurrency()
        );

        Long productId = productRepository.save(product);

        // 상품 저장 이후 이미지 저장
        if (imageFiles != null && imageFiles.length > 0) {
            imageStorage.store(productId, imageFiles);
        }

        return productId;
    }

    @Override
    public List<ProductListItemDto> searchProducts(ProductSearchConditionDto condition, int page, int size) {
        return productRepository.findProducts(condition, page, size);
    }

    @Override
    public Long getTotalProductCount(ProductSearchConditionDto condition) {
        return productRepository.countProducts(condition);
    }

    @Override
    public ProductDetailDto showProduct(Long productId) {
        ProductDetailDto findProduct = productRepository.findProductById(productId);

        if(findProduct == null) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다. : " + productId);
        }

        return findProduct;
    }

    @Override
    @Transactional
    public void modifyProduct(ProductUpdateDto productUpdateDto, MultipartFile[] imageFiles, boolean removeImageFiles) {
        Product updateTargetProduct = productRepository.findProductByIdForUpdate(productUpdateDto.getId());

        if(updateTargetProduct == null) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다. : " + productUpdateDto.getId());
        }

        updateTargetProduct.updateBasicInfo(
                productUpdateDto.getName(),
                productUpdateDto.getSlug(),
                productUpdateDto.getDescription(),
                productUpdateDto.getStatus(),
                productUpdateDto.getBasePrice(),
                productUpdateDto.getCurrency()
        );

        if (removeImageFiles) {
            imageStorage.clearAll(productUpdateDto.getId());
        }

        if (imageFiles != null && imageFiles.length > 0) {
            imageStorage.store(productUpdateDto.getId(), imageFiles);
        }
    }

    @Override
    @Transactional
    public void modifyProductInventoryStock(Long productId, Integer stockQuantity) {
        if (stockQuantity == null) {
            throw new IllegalArgumentException("재고는 필수입니다.");
        }

        Inventory productWithInventory = productRepository.findProductByIdForUpdateInventoryStock(productId);

        if(productWithInventory == null) {
            throw new ProductNotFoundException("아이템이 없습니다: " + productId);
        }

        productWithInventory.updateStockQuantity(stockQuantity);
    }

    @Override
    public ProductHeaderDto showProductHeader(Long productId) {
        Product product = productRepository.findById(productId);

        return new ProductHeaderDto(
                product.getId(),
                product.getName()
        );
    }

    @Override
    public Product findById(Long productId) {
        return productRepository.findById(productId);
    }
}
