package sample.myshop.order.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.myshop.common.entity.CommonEntity;
import sample.myshop.enums.order.OrderStatus;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

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
    private OrderStatus status = OrderStatus.ORDERED;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @OneToMany(mappedBy = "order",
            cascade = CascadeType.PERSIST
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    private Order(String orderNo, String buyerLoginId) {
        this.orderNo = orderNo;
        this.memberId = null; // 일단 null
        this.buyerLoginId = buyerLoginId;
        this.status = OrderStatus.ORDERED;
        this.totalAmount = 0;
        this.orderItems = new ArrayList<>();
    }

    public static Order createOrder(String orderNo, String buyerLoginId) {
        if (orderNo == null || orderNo.isBlank()) {
            throw new IllegalArgumentException("주문번호는 필수입니다.");
        }

        if (buyerLoginId == null || buyerLoginId.isBlank()) {
            throw new IllegalArgumentException("주문자 아이디는 필수입니다.");
        }

        return new Order(orderNo, buyerLoginId);
    }

    /**
     * 연관관계 편의 메소드
     */
    public void addOrderItem(OrderItem orderItem) {
        if(orderItem == null) {
            throw new IllegalArgumentException("주문 상품은 필수입니다.");
        }

        orderItems.add(orderItem);
        orderItem.assignOrder(this);

        totalAmount += orderItem.getLineAmount();
    }
}
