package sample.myshop.shop.order.domain.dto.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderPrepareForm {
    @NotNull
    private Long productId;

    @NotNull
    private Long variantId;

    @Min(1)
    private int quantity;
}
