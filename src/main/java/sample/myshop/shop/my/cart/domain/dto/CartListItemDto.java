package sample.myshop.shop.my.cart.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CartListItemDto {
    private Long cartItemId;
    private Long productId;
    private Long variantId;

    private String productName;
    private String sku;

    private Integer unitPrice;
    private Integer quantity;
    private Integer lineAmount;

    private Integer stockQuantity;
    private boolean orderable;

    public CartListItemDto(
            Long cartItemId,
            Long productId,
            Long variantId,
            String productName,
            String sku,
            Integer unitPrice,
            Integer quantity,
            Integer stockQuantity) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.variantId = variantId;
        this.productName = productName;
        this.sku = sku;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineAmount = (unitPrice != null && quantity != null) ? unitPrice * quantity : 0;
        this.stockQuantity = stockQuantity;
        this.orderable = stockQuantity != null && stockQuantity >= quantity;
    }
}
