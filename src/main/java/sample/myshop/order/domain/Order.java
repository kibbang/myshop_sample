package sample.myshop.order.domain;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import sample.myshop.common.entity.CommonEntity;
import sample.myshop.enums.order.OrderStatus;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Table(name = "ord_orders")
@NoArgsConstructor(access = PROTECTED)
public class Order extends CommonEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(name = "order_no", unique = true, length = 20, nullable = false)
    private String orderNo;
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


    // TODO orderNo/buyerLoginId 세팅, status는 기본 ORDERED 유지, totalAmount 0 시작
    public void createOrder() {

    }


    /** 연관관계 편의 메소드 */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.assignOrder(this);

        totalAmount += orderItem.getLineAmount();
    }
}
