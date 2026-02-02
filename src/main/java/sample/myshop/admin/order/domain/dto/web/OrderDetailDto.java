package sample.myshop.admin.order.domain.dto.web;

import lombok.Getter;
import sample.myshop.enums.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderDetailDto {
    private Long id;
    private String orderNo;
    private String buyerLoginId;
    private OrderStatus status;
    private int totalAmount;
    private LocalDateTime createdAt;
    private List<OrderItemDto> orderItems;

    private OrderDetailDto(Long id, String orderNo, String buyerLoginId, OrderStatus status, int totalAmount, LocalDateTime createdAt, List<OrderItemDto> orderItems) {
        this.id = id;
        this.orderNo = orderNo;
        this.buyerLoginId = buyerLoginId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.orderItems = orderItems;
    }

    public static OrderDetailDto of(Long id, String orderNo, String buyerLoginId, OrderStatus status, int totalAmount, LocalDateTime createdAt, List<OrderItemDto> orderItems) {
        return new OrderDetailDto(id, orderNo, buyerLoginId, status, totalAmount, createdAt, orderItems);
    }
}
