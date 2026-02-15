package sample.myshop.admin.product.domain.dto.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OptionDto {
    private Long id;
    private String name;
    private int sortOrder;
    private List<OptionValueDto> optionValues;
}
