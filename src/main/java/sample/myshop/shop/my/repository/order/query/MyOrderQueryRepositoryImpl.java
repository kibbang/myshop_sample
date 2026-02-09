package sample.myshop.shop.my.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.order.domain.Order;
import sample.myshop.shop.my.domain.dto.MyOrderListDto;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyOrderQueryRepositoryImpl implements MyOrderQueryRepository {

    private final EntityManager em;

    @Override
    public List<MyOrderListDto> getMyOrderList(Long memberId) {
        List<Order> orderList = em.createQuery("select o" +
                                " from Order o" +
                                " left join fetch o.release r" +
                                " where o.memberId = :memberId order by o.createdAt desc",
                        Order.class)
                .setParameter("memberId", memberId)
                .setMaxResults(20)
                .getResultList();

        return orderList.stream()
                .map(order -> {

                    var release = order.getRelease();
                    var releaseStatus = (release == null) ? null : release.getStatus();

                    return new MyOrderListDto(
                            order.getOrderNo(),
                            order.getCreatedAt(),
                            order.getTotalAmount(),
                            order.getStatus(),
                            releaseStatus,
                            order.getReceiverName(),
                            order.getReceiverPhone()
                    );
                })
                .toList();
    }
}
