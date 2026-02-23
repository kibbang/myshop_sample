package sample.myshop.shop.my.cart.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class CartListDto {
    private List<CartListItemDto> items = new ArrayList<>();
    private Integer totalAmount = 0;

    public CartListDto(List<CartListItemDto> items) {
        this.items = items;
        this.totalAmount = items.stream()
                .map(CartListItemDto::getLineAmount)
                .filter(amount -> amount != null)
                .reduce(0, Integer::sum);
    }

    public boolean isEmpty(){
        return items == null || items.isEmpty();
    }
}
