package sample.myshop.admin.product.domain.dto.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sample.myshop.enums.product.Currency;
import sample.myshop.enums.product.SaleStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ProductDetailDto {
    private final Long id;
    private final String code;
    private final String name;
    private final SaleStatus status;
    private final int basePrice;
    private final Currency currency;
    private final String slug;
    private final String description;
    private final String sku;
    private final Integer stockQuantity;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ProductDetailDto of(
            Long id,
            String code,
            String name,
            SaleStatus status,
            int basePrice,
            Currency currency,
            String slug,
            String description,
            String sku,
            Integer stockQuantity,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new ProductDetailDto(
                id,
                code,
                name,
                status,
                basePrice,
                currency,
                slug,
                description,
                sku,
                stockQuantity,
                createdAt,
                updatedAt
        );
    }

    public String getPrimaryImageUrl() {

        String purge = (updatedAt != null)
                ? updatedAt.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                : String.valueOf(System.currentTimeMillis());

        return "/uploads/products/" + id + "/1.jpg?purge=" + purge;
    }
}
