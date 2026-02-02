package sample.myshop.admin.order.domain.dto.web;

import lombok.Getter;

@Getter
public class OrderItemDto {
    private Long productId;
    private Long variantId;
    private String skuSnapshot;
    private String productNameSnapshot;
    private int unitPriceSnapshot;
    private int quantity;
    private int lineAmount;

    private OrderItemDto(Long productId, Long variantId, String skuSnapshot, String productNameSnapshot, int unitPriceSnapshot, int quantity, int lineAmount) {
        this.productId = productId;
        this.variantId = variantId;
        this.skuSnapshot = skuSnapshot;
        this.productNameSnapshot = productNameSnapshot;
        this.unitPriceSnapshot = unitPriceSnapshot;
        this.quantity = quantity;
        this.lineAmount = lineAmount;
    }

    public static OrderItemDto of(Long productId, Long variantId, String skuSnapshot, String productNameSnapshot, int unitPriceSnapshot, int quantity, int lineAmount) {
        return new OrderItemDto(productId, variantId, skuSnapshot, productNameSnapshot, unitPriceSnapshot, quantity, lineAmount);
    }
}
