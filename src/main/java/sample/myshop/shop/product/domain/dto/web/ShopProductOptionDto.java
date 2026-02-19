package sample.myshop.shop.product.domain.dto.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ShopProductOptionDto {
    private Long id;
    private String name;
    private List<ShopProductOptionValueDto> optionValues;
}
