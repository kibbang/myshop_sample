package sample.myshop.order.domain.dto;

import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class VariantSnapshotDto {
    private Long variantId;
    private String sku;
    private Integer variantPrice;
    private String productName;
    private int basePrice;

    public VariantSnapshotDto(Long variantId, String sku, @Nullable Integer variantPrice, String productName, int basePrice) {
        this.variantId = variantId;
        this.sku = sku;
        this.variantPrice = variantPrice;
        this.productName = productName;
        this.basePrice = basePrice;
    }
}


