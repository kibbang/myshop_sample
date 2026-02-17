package sample.myshop.admin.product.domain.dto.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VariantInfoDto {
    private Long id;
    private boolean isDefault;
    private String sku;
    private int customPrice;
    private int stockQuantity;
    private String optionSummary;
}
