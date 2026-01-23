package sample.myshop.admin.product.enums;

import lombok.Getter;

@Getter
public enum SaleStatus {
    DRAFT("대기"),
    ACTIVE("판매 중"),
    INACTIVE("판매중지");

    private final String label;

    SaleStatus(String label) {
        this.label = label;
    }
}
