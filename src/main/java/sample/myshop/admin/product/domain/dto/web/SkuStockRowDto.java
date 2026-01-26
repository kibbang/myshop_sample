package sample.myshop.admin.product.domain.dto.web;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkuStockRowDto {
    private final Long productId;
    private final String sku;
    private final Integer stockQuantity;
}
