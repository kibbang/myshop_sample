package sample.myshop.shop.order.domain.dto.web;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPrepareItemDto {
    private Long productId;
    private String productName;
    private Long variantId;
    private String sku;
    private Long unitPrice;
    private Integer quantity;
    private Long lineAmount;
    private String imageUrl;
}
