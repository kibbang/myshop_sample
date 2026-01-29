package sample.myshop.order.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.myshop.common.entity.CommonEntity;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.*;

@Entity
@Table(name = "ord_order_items")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class OrderItem extends CommonEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "variant_id", nullable = false)
    private Long variantId;

    @Column(name = "sku_snapshot", nullable = false)
    private String skuSnapshot;
    @Column(name = "product_name_snapshot", nullable = false)
    private String productNameSnapshot;
    @Column(name = "unit_price", nullable = false)
    private int unitPrice;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @Column(name = "line_amount", nullable = false)
    private int lineAmount; // unitPrice * quantity

    //TODO 생성 + 검증 + lineAmount 계산
    public void createOrderItem() {

    }

    /** 연관관계 편의 메소드 */
    public void assignOrder(Order order) {
        this.order = order;
        // 여기서도 order세팅하면 무한루프
    }
}
