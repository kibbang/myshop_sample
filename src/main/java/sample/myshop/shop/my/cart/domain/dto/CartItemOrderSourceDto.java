package sample.myshop.shop.my.cart.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartItemOrderSourceDto {
    private Long cartItemId;
    private Long productId;
    private Long variantId;
    private Integer quantity;
}
