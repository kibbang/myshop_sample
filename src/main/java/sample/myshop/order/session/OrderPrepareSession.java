package sample.myshop.order.session;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderPrepareSession {
    private Long productId;
    private int quantity;
}
