package sample.myshop.admin.product.domain.dto.web;

import lombok.Getter;
import sample.myshop.admin.product.enums.Currency;
import sample.myshop.admin.product.enums.SaleStatus;

@Getter
public class ProductCreateDto {
    private final String code;
    private final String name;
    private final String slug;
    private final String description;
    private final SaleStatus status;
    private final int basePrice;
    private final Currency currency;

    private ProductCreateDto(String code, String name, String slug, String description, SaleStatus saleStatus, int basePrice, Currency currency) {
        this.code = code;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.status = saleStatus;
        this.basePrice = basePrice;
        this.currency = currency;
    }

    public static ProductCreateDto of(String code, String name, String slug, String description, SaleStatus saleStatus, int basePrice, Currency currency) {
        return new ProductCreateDto(code, name, slug, description, saleStatus, basePrice, currency);
    }
}
