package sample.myshop.admin.product.service;

import org.springframework.web.multipart.MultipartFile;
import sample.myshop.admin.product.domain.dto.web.*;

import java.util.List;

public interface ProductService {
    Long createProduct(ProductCreateDto productCreateDto, MultipartFile[] imageFile);
    List<ProductListItemDto> searchProducts(ProductSearchConditionDto condition, int page, int size);
    Long getTotalProductCount(ProductSearchConditionDto condition);

    ProductDetailDto showProduct(Long productId);
    void modifyProduct(ProductUpdateDto productUpdateDto, MultipartFile[] imageFiles, boolean removeImageFiles);

    void modifyProductInventoryStock(Long productId, Integer stockQuantity);

    ProductHeaderDto showProductHeader(Long productId);
}
