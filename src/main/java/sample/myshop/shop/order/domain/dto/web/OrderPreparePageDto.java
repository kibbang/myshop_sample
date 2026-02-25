package sample.myshop.shop.order.domain.dto.web;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderPreparePageDto {
    private List<OrderPrepareItemDto> items = new ArrayList<>();
    private Long totalAmount = 0L;
    private Integer totalQuantity = 0;
    private Integer itemCount = 0;

    // 주문 프리페어 아이템 정보 추가
    public void addItem(OrderPrepareItemDto item) {
        this.items.add(item);

        if (item.getLineAmount() != null) {
            this.totalAmount += item.getLineAmount();
        }

        if (item.getQuantity() != null) {
            this.totalQuantity += item.getQuantity();
        }

        this.itemCount = this.items.size();
    }
}
