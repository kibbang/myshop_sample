package sample.myshop.shop.product.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductVariantMatchResponse {
    private boolean matched;
    private Long variantId;
    private String sku;
    private int stockQuantity;
    private boolean orderable;
}
