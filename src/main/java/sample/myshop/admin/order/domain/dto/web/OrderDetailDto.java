package sample.myshop.admin.order.domain.dto.web;

import lombok.Getter;
import sample.myshop.enums.order.OrderStatus;
import sample.myshop.release.enums.ReleaseStatus;

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
    private String receiverName;
    private String receiverPhone;
    private String receiverZip;
    private String receiverBaseAddress;
    private String receiverDetailAddress;
    private String deliveryMemo;
    private ReleaseStatus releaseStatus;

    private OrderDetailDto(
            Long id,
            String orderNo,
            String buyerLoginId,
            OrderStatus status,
            int totalAmount,
            LocalDateTime createdAt,
            List<OrderItemDto> orderItems,
            String receiverName,
            String receiverPhone,
            String receiverZip,
            String receiverBaseAddress,
            String receiverDetailAddress,
            String deliveryMemo,
            ReleaseStatus releaseStatus
    ) {
        this.id = id;
        this.orderNo = orderNo;
        this.buyerLoginId = buyerLoginId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.orderItems = orderItems;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverZip = receiverZip;
        this.receiverBaseAddress = receiverBaseAddress;
        this.receiverDetailAddress = receiverDetailAddress;
        this.deliveryMemo = deliveryMemo;
        this.releaseStatus = releaseStatus;
    }

    public static OrderDetailDto of(
            Long id,
            String orderNo,
            String buyerLoginId,
            OrderStatus status,
            int totalAmount,
            LocalDateTime createdAt,
            List<OrderItemDto> orderItems,
            String receiverName,
            String receiverPhone,
            String receiverZip,
            String receiverBaseAddress,
            String receiverDetailAddress,
            String deliveryMemo,
            ReleaseStatus releaseStatus
    ) {
        return new OrderDetailDto(
                id,
                orderNo,
                buyerLoginId,
                status,
                totalAmount,
                createdAt,
                orderItems,
                receiverName,
                receiverPhone,
                receiverZip,
                receiverBaseAddress,
                receiverDetailAddress,
                deliveryMemo,
                releaseStatus
        );
    }
}
