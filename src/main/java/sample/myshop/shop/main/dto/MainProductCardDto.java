package sample.myshop.shop.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sample.myshop.enums.product.Currency;

@Getter
@AllArgsConstructor
public class MainProductCardDto {
    private Long id;
    private String name;
    private String slug;
    private int price;
    private Currency currency;
    private String sku;
    private int stockQuantity;

    public boolean isSoldOut() {
        return stockQuantity <= 0;
    }
}
