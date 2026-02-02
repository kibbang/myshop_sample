package sample.myshop.admin.order.domain.dto.web;

import jakarta.annotation.Nullable;
import lombok.Getter;
import sample.myshop.common.entity.PaginatorEntity;
import sample.myshop.enums.order.OrderStatus;

@Getter
public class OrderSearchConditionDto extends PaginatorEntity {
    private final String keyword; // like
    private final OrderStatus status;

    private OrderSearchConditionDto(@Nullable String keyword, @Nullable OrderStatus status) {
        this.keyword = keyword;
        this.status = status;
    }

    /**
     * 생성 메소드
     * @param keyword
     * @param status
     * @return
     */
    public static OrderSearchConditionDto of(String keyword, OrderStatus status) {
        return new OrderSearchConditionDto(keyword, status);
    }
}
