package sample.myshop.order.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sample.myshop.enums.order.OrderSourceType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static sample.myshop.enums.order.OrderSourceType.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderPrepareSession {
    List<OrderPrepareLineSession> lines = new ArrayList<>();
    OrderSourceType orderSourceType;
    List<Long> cartItemIds = new ArrayList<>();
    LocalDateTime preparedAt;

    public static OrderPrepareSession directSingle(Long productId, Long variantId, Integer quantity) {
        OrderPrepareSession session = new OrderPrepareSession();
        session.orderSourceType = DIRECT;
        session.lines.add(OrderPrepareLineSession.of(productId, variantId, quantity));
        session.preparedAt = LocalDateTime.now();
        return session;
    }

    public static OrderPrepareSession fromCart(List<OrderPrepareLineSession> lines, List<Long> cartItemIds) {
        OrderPrepareSession session = new OrderPrepareSession();
        session.orderSourceType = CART;
        session.lines = new ArrayList<>(lines);
        session.cartItemIds = (cartItemIds == null) ? new ArrayList<>() : new ArrayList<>(cartItemIds);
        session.preparedAt = LocalDateTime.now();
        return session;
    }

    public boolean isCart() {
        return orderSourceType == CART;
    }

    public boolean isEmpty() {
        return lines == null || lines.isEmpty();
    }

}
