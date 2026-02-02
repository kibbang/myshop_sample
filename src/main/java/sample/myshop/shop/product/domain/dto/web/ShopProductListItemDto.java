package sample.myshop.shop.product.domain.dto.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sample.myshop.enums.product.Currency;

@Getter
@AllArgsConstructor
public class ShopProductListItemDto {
    private Long Id;
    private String name;
    private int basePrice;
    private Currency currency;
}
