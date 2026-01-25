package sample.myshop.admin.product.domain.dto.web;

import lombok.Getter;
import sample.myshop.admin.product.enums.Currency;
import sample.myshop.admin.product.enums.SaleStatus;

import java.time.LocalDateTime;

@Getter
public class ProductListItemDto {
    private final Long id;
    private final String code;
    private final String name;
    private final SaleStatus status;
    private final int basePrice;
    private final Currency currency;
    private final LocalDateTime createdAt;

    private ProductListItemDto(Long id, String code, String name, SaleStatus status, int basePrice, Currency currency, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.basePrice = basePrice;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    /**
     * 생성 메소드
     * @param id
     * @param code
     * @param name
     * @param status
     * @param basePrice
     * @param currency
     * @param createdAt
     * @return
     */
    public static ProductListItemDto of(Long id, String code, String name, SaleStatus status, int basePrice, Currency currency, LocalDateTime createdAt) {
        return new ProductListItemDto(id, code, name, status, basePrice, currency, createdAt);
    }
}
