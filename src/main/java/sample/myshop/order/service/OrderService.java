package sample.myshop.order.service;

public interface OrderService {
    String placeOrder(Long productId, int quantity, String buyerLoginId);

    void cancelOrder(Long orderId);

    void getOrder(Long orderId);
}
