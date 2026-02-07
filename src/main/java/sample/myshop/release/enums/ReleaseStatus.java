package sample.myshop.release.enums;

import lombok.Getter;

@Getter
public enum ReleaseStatus {
    READY("준비중"),
    RELEASED("출고 완료"),
    DELIVERED("배송완료"),
    CANCELED("취소");

    private final String label;

    ReleaseStatus(String label) {
        this.label = label;
    }
}
