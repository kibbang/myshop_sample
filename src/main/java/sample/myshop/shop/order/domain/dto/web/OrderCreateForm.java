package sample.myshop.shop.order.domain.dto.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderCreateForm {
    @NotNull
    private Long productId;

    @Min(1)
    private int quantity;
}
