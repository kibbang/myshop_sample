package sample.myshop.admin.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sample.myshop.enums.order.OrderStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecentOrderItemDto {
    private Long orderId;
    private String orderNo;
    private String buyerName; // 없으면 buyerLoginId 넣어도 됨
    private OrderStatus orderStatus;
    private int totalAmount;
    private LocalDateTime orderedAt;
}
