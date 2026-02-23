package sample.myshop.order.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.myshop.common.entity.CommonEntity;
import sample.myshop.common.exception.BadRequestException;
import sample.myshop.common.exception.NotFoundException;
import sample.myshop.enums.order.OrderStatus;
import sample.myshop.release.domain.OrderRelease;
import sample.myshop.release.enums.ReleaseStatus;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;
import static sample.myshop.enums.order.OrderStatus.*;

@Entity
@Getter
@Table(name = "ord_orders")
@NoArgsConstructor(access = PROTECTED)
public class Order extends CommonEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "order_no", unique = true, length = 20, nullable = false)
    private String orderNo;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "buyer_login_id", nullable = false)
    private String buyerLoginId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = ORDERED;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @OneToMany(mappedBy = "order",
            cascade = CascadeType.PERSIST
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @OneToOne(mappedBy = "order", cascade =  CascadeType.PERSIST, fetch = FetchType.LAZY)
    private OrderRelease release;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "receiver_phone")
    private String receiverPhone;

    @Column(name = "receiver_zip")
    private String receiverZip;

    @Column(name = "receiver_base_address")
    private String receiverBaseAddress;

    @Column(name = "receiver_detail_address")
    private String receiverDetailAddress;

    @Column(name = "delivery_memo")
    private String deliveryMemo;

    private Order(
            String orderNo,
            String buyerLoginId,
            Long memberId,
            String receiverName,
            String receiverPhone,
            String receiverZip,
            String receiverBaseAddress,
            String receiverDetailAddress,
            String deliveryMemo
    ) {
        this.orderNo = orderNo;
        this.memberId = memberId;
        this.buyerLoginId = buyerLoginId;
        this.status = ORDERED;
        this.totalAmount = 0;
        this.orderItems = new ArrayList<>();
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverZip = receiverZip;
        this.receiverBaseAddress = receiverBaseAddress;
        this.receiverDetailAddress = receiverDetailAddress;
        this.deliveryMemo = deliveryMemo;
    }

    public static Order createOrder(
            String orderNo,
            String buyerLoginId,
            Long memberId,
            String receiverName,
            String receiverPhone,
            String receiverZip,
            String receiverBaseAddress,
            String receiverDetailAddress,
            String deliveryMemo
    ) {
        if (orderNo == null || orderNo.isBlank()) {
            throw new BadRequestException("주문번호는 필수입니다.");
        }

        if (memberId == null) {
            throw new NotFoundException("주문 회원이 없습니다.");
        }


        if (buyerLoginId == null || buyerLoginId.isBlank()) {
            throw new BadRequestException("주문자 아이디는 필수입니다.");
        }

        return new Order(
                orderNo,
                buyerLoginId,
                memberId,
                receiverName,
                receiverPhone,
                receiverZip,
                receiverBaseAddress,
                receiverDetailAddress,
                deliveryMemo
        );
    }

    /**
     * 연관관계 편의 메소드
     */
    public void addOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            throw new BadRequestException("주문 상품은 필수입니다.");
        }

        orderItems.add(orderItem);
        orderItem.assignOrder(this);

        totalAmount += orderItem.getLineAmount();
    }

    public void createRelease(OrderRelease orderRelease) {
        release = orderRelease;

        orderRelease.assignOrder(this);
    }

    /**
     * 주문 상태 변경 (주문 -> 취소)
     */
    public void cancel() {
        //  주문 상태 체크
        if (status != ORDERED) {
            throw new BadRequestException("주문 상태가 '" + ORDERED.getLabel() + "'이어야 합니다.");
        }

        // 주문 체크 이후 출고 단위 존재 여부 체크
        if (release == null) {
            throw new NotFoundException("출고 정보가 없습니다.");
        }

        // 출고 상태 변경
        release.toCancel();

        // 출고 상태 변경 이상 없을 경우 주문 상태 변경
        status = CANCELED;
    }
}
