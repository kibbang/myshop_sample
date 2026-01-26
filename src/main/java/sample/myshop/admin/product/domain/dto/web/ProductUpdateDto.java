package sample.myshop.admin.product.domain.dto.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sample.myshop.admin.product.enums.Currency;
import sample.myshop.admin.product.enums.SaleStatus;

import static lombok.AccessLevel.*;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class ProductUpdateDto {
    private final Long id;
    private final String name;
    private final String slug;
    private final String description;
    private final SaleStatus status;
    private final int basePrice;
    private final Currency currency;

    public static ProductUpdateDto of(Long id, String name, String slug, String description, SaleStatus saleStatus, int basePrice, Currency currency) {
        return new ProductUpdateDto(id, name, slug, description, saleStatus, basePrice, currency);
    }
}
