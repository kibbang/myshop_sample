package sample.myshop.admin.product.domain.dto.web;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VariantCreateDto {
    private Long productId;
    private String sku;
    private int customPrice;
}
