package sample.myshop.shop.my.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyOrderItemDto {
    private String productNameSnapshot;
    private String skuSnapshot;
    private int unitPriceSnapshot;
    private int quantity;
    private int lineAmount;
}
