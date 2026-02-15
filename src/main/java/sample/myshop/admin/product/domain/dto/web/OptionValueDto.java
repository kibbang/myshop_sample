package sample.myshop.admin.product.domain.dto.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OptionValueDto {
    private Long id;
    private String value;
    private int sortOrder;
}
