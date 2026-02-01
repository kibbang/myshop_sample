package sample.myshop.order.service;

public interface OrderService {
    String placeOrder(Long productId, int quantity, String buyerLoginId);
}
