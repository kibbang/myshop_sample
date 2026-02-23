package sample.myshop.shop.order.domain.dto.web;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderPrepareInfoDto {
    private Long productId;
    private Long variantId;
    private String productName;
    private String sku;
    private String optionSummary;
    private int quantity;
    private int stockQuantity;
    private long unitPrice;
    private long lineAmount;
}
