package sample.myshop.enums.order;

import lombok.Getter;

@Getter
public enum OrderStatus {
    ORDERED("주문"),
    CANCELED("취소");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }
}
