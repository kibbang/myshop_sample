package sample.myshop.admin.product.enums;

import lombok.Getter;

@Getter
public enum Currency {
    KRW("원"),
    USD("달러"),
    JPY("엔");

    private final String label;

    Currency(String label) {
        this.label = label;
    }

}
