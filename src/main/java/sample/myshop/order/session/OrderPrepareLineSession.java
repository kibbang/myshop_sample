package sample.myshop.order.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.*;

/**
 * 한 상품/ 한 옵션 / 수량 이 렇게 라인 단위로 담을 수 있도록 하는 객체
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderPrepareLineSession {
    private Long productId;
    private Long variantId;
    private Integer quantity;

    private OrderPrepareLineSession(Long productId, Long variantId, int quantity) {
        this.productId = productId;
        this.variantId = variantId;
        this.quantity = quantity;
    }

    public static OrderPrepareLineSession of(Long productId, Long variantId, int quantity) {
        return new OrderPrepareLineSession(productId, variantId, quantity);
    }
}
