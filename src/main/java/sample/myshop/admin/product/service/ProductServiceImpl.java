package sample.myshop.admin.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.admin.product.domain.Product;
import sample.myshop.admin.product.domain.dto.web.ProductCreateDto;
import sample.myshop.admin.product.domain.dto.web.ProductListItemDto;
import sample.myshop.admin.product.domain.dto.web.ProductSearchConditionDto;
import sample.myshop.admin.product.repository.ProductRepository;

import java.util.List;

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
}
