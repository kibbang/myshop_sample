package sample.myshop.admin.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.admin.product.domain.Product;
import sample.myshop.admin.product.domain.dto.web.*;
import sample.myshop.admin.product.repository.ProductRepository;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Long createProduct(ProductCreateDto productCreateDto) {
        Product product = Product.createProduct(
                productCreateDto.getCode(),
                productCreateDto.getName(),
                productCreateDto.getSlug(),
                productCreateDto.getDescription(),
                productCreateDto.getStatus(),
                productCreateDto.getBasePrice(),
                productCreateDto.getCurrency()
        );

        return productRepository.save(product);
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
            throw new IllegalArgumentException("Product not found with id: " + productId);
        }

        return findProduct;
    }

    @Override
    @Transactional
    public void modifyProduct(ProductUpdateDto productUpdateDto) {
        Product updateTargetProduct = productRepository.findProductByIdForUpdate(productUpdateDto.getId());

        if(updateTargetProduct == null) {
            throw new IllegalArgumentException("Product not found with id: " + productUpdateDto.getId());
        }

        updateTargetProduct.updateBasicInfo(
                productUpdateDto.getName(),
                productUpdateDto.getSlug(),
                productUpdateDto.getDescription(),
                productUpdateDto.getStatus(),
                productUpdateDto.getBasePrice(),
                productUpdateDto.getCurrency()
        );
    }
}
