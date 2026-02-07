package sample.myshop.admin.release.repository;

import sample.myshop.release.domain.OrderRelease;

public interface ReleaseRepository {
    // 주문 번호로 배송 찾기
    OrderRelease findByOrderId(Long orderId);
}
