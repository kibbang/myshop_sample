package sample.myshop.admin.order.domain.dto.web;

import lombok.Getter;
import sample.myshop.enums.order.OrderStatus;

import java.time.LocalDateTime;

@Getter
public class OrderListItemDto {
    private final Long id;
    private final String orderNo;
    private final String buyerLoginId;
    private final OrderStatus status;
    private final int totalAmount;
    private final LocalDateTime createdAt;

    private OrderListItemDto(Long id, String orderNo, String buyerLoginId, OrderStatus status, int totalAmount, LocalDateTime createdAt) {
        this.id = id;
        this.orderNo = orderNo;
        this.buyerLoginId = buyerLoginId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public static OrderListItemDto of(Long id, String orderNo, String buyerLoginId, OrderStatus status, int totalAmount, LocalDateTime createdAt) {
        return new OrderListItemDto(id, orderNo, buyerLoginId, status, totalAmount, createdAt);
    }
}
