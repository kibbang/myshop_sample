package sample.myshop.admin.product.domain.dto.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VariantListDto {
    private Long id;
    private String sku;
    private int customPrice;
    private int stockQuantity;
}
