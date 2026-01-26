package sample.myshop.admin.product.domain.dto.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sample.myshop.admin.product.enums.Currency;
import sample.myshop.admin.product.enums.SaleStatus;

@Getter
@Setter
@NoArgsConstructor
public class ProductUpdateRequestDto {
    @NotBlank
    private String name;

    @NotNull
    private SaleStatus status;

    @Min(100)
    @Max(100000000)
    private int basePrice;

    @NotNull
    private Currency currency;
    private String slug;
    private String description;
}
