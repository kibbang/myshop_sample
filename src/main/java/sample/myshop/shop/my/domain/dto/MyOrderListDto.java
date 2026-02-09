package sample.myshop.shop.my.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sample.myshop.enums.order.OrderStatus;
import sample.myshop.release.enums.ReleaseStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyOrderListDto {
    private String orderNo;
    private LocalDateTime createdAt;
    private int totalAmount;
    private OrderStatus orderStatus;
    private ReleaseStatus releaseStatus;
    private String receiverName;
    private String receiverPhone;
}
