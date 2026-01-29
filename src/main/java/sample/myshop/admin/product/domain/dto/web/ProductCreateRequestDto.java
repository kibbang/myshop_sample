package sample.myshop.admin.product.domain.dto.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sample.myshop.enums.product.Currency;
import sample.myshop.enums.product.SaleStatus;

@Getter
@Setter
@NoArgsConstructor
public class ProductCreateRequestDto {
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    private String slug;
    @NotBlank
    private String description;
    @NotNull
    private SaleStatus status;

    @Min(100)
    @Max(100000000)
    private int basePrice;
    @NotNull
    private Currency currency;
}
