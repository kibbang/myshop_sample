package sample.myshop.shop.product.domain.dto.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sample.myshop.enums.product.Currency;

@Getter
@AllArgsConstructor
public class ShopProductDetailDto {
    private Long id;
    private String name;
    private String description;
    private int basePrice;
    private Currency currency;
}
