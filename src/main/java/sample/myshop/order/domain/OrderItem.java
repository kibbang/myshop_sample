package sample.myshop.order.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.myshop.common.entity.CommonEntity;
import sample.myshop.common.exception.BadRequestException;

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

    private OrderItem(Long productId, Long variantId, String skuSnapshot, String productNameSnapshot, int unitPrice, int quantity) {
        this.productId = productId;
        this.variantId = variantId;
        this.skuSnapshot = skuSnapshot;
        this.productNameSnapshot = productNameSnapshot;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineAmount = unitPrice * quantity; // 내부 계산
    }

    public static OrderItem createOrderItem(
            Long productId,
            Long variantId,
            String skuSnapshot,
            String productNameSnapshot,
            int unitPrice,
            int quantity
    ) {
        if (productId == null) {
            throw new BadRequestException("상품은 필수 입니다.");
        }

        if (variantId == null) {
            throw new BadRequestException("상품의 SKU(Variant)는 필수 입니다.");
        }

        if (skuSnapshot == null || skuSnapshot.isBlank()) {
            throw new BadRequestException("상품의 SKU는 필수 입니다.");
        }

        if (productNameSnapshot == null || productNameSnapshot.isBlank()) {
            throw new BadRequestException("상품의 이름은 필수 입니다.");
        }

        if (unitPrice <= 0) {
            throw new BadRequestException("상품의 가격은 0보다 커야 합니다.");
        }

        if (quantity <= 0) {
            throw new BadRequestException("상품의 수량은 0보다 커야 합니다.");
        }

        return new OrderItem(productId, variantId, skuSnapshot, productNameSnapshot, unitPrice, quantity);
    }

    /** 연관관계 편의 메소드 */
    public void assignOrder(Order order) {
        this.order = order;
        // 여기서도 order세팅하면 무한루프
    }
}
