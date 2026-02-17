package sample.myshop.admin.product.domain.dto.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AdminProductVariantCreateRequestDto {
    @NotEmpty(message = "옵션값을 선택하세요.")
    private List<Long> optionValueIds;
    private String sku;
    @Min(value = 0, message = "옵션가는 0 이상이여야 합니다.")
    private int customPrice;
}
