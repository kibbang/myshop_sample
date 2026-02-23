package sample.myshop.shop.my.cart.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.myshop.common.entity.CommonEntity;
import sample.myshop.common.exception.BadRequestException;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ord_cart_items")
public class CartItem extends CommonEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "session_id", length = 128)
    private String sessionId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "variant_id", nullable = false)
    private Long variantId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public static CartItem create(String sessionId, Long memberId, Long productId, Long variantId, int quantity) {
        if ((sessionId == null || sessionId.isBlank()) && memberId == null) {
            throw new BadRequestException("장바구니 식별자(session/member)가 없습니다.");
        }
        if (productId == null || variantId == null) {
            throw new BadRequestException("상품/옵션 정보가 없습니다.");
        }
        if (quantity < 1) {
            throw new BadRequestException("수량은 1개 이상이어야 합니다.");
        }

        CartItem item = new CartItem();
        item.sessionId = (sessionId == null || sessionId.isBlank()) ? null : sessionId;
        item.memberId = memberId;
        item.productId = productId;
        item.variantId = variantId;
        item.quantity = quantity;
        return item;
    }

    public void increaseQuantity(int ea) {
        if (ea < 1) {
            throw new BadRequestException("추가 수량은 1개 이상이어야 합니다.");
        }
        this.quantity += ea;
    }

    public void changeQuantity(int ea) {
        if (ea < 1) {
            throw new BadRequestException("수량은 1개 이상이어야 합니다.");
        }
        this.quantity = ea;
    }

    public void attachMember(Long memberId) {
        if (memberId == null) {
            throw new BadRequestException("회원 정보가 없습니다.");
        }
        this.memberId = memberId;
    }

    public boolean isOwnedBy(Long memberId, String sessionId) {
        if (memberId != null && this.memberId != null) {
            return this.memberId.equals(memberId);
        }
        return this.memberId == null
                && this.sessionId != null
                && sessionId != null
                && this.sessionId.equals(sessionId);
    }
}
