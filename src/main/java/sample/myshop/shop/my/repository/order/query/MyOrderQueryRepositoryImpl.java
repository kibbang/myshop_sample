package sample.myshop.shop.my.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;
import sample.myshop.order.domain.Order;
import sample.myshop.order.domain.OrderItem;
import sample.myshop.release.domain.OrderRelease;
import sample.myshop.release.enums.ReleaseStatus;
import sample.myshop.shop.my.domain.dto.MyOrderDetailDto;
import sample.myshop.shop.my.domain.dto.MyOrderItemDto;
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

    @Override
    public MyOrderDetailDto getMyOrderDetail(String orderNo, Long memberId) {
        MyOrderFetch fetchedOrderDetails = fetchOrderDetails(orderNo, memberId);
        Order order = fetchedOrderDetails.order;

        // 주문 아이템
        List<MyOrderItemDto> myOrderItemDtoList = order.getOrderItems().stream()
                .map(orderItem -> new MyOrderItemDto(
                orderItem.getProductNameSnapshot(),
                orderItem.getSkuSnapshot(),
                orderItem.getUnitPrice(),
                orderItem.getQuantity(),
                orderItem.getLineAmount()
        ))
                .toList();

        return new MyOrderDetailDto(
                order.getOrderNo(),
                order.getCreatedAt(),
                order.getTotalAmount(),
                order.getStatus(),
                fetchedOrderDetails.releaseStatus(),
                order.getReceiverName(),
                order.getReceiverPhone(),
                order.getReceiverZip(),
                order.getReceiverBaseAddress(),
                order.getReceiverDetailAddress(),
                order.getDeliveryMemo(),
                myOrderItemDtoList
        );
    }

    @Override
    public Order cancel(String orderNo, Long memberId) {
        MyOrderFetch fetchedOrderDetails = fetchOrderDetails(orderNo, memberId);

        return fetchedOrderDetails.order;
    }


    /**
     * 주문 상세 조회
     * @param orderNo
     * @param memberId
     * @return
     */
    private MyOrderFetch fetchOrderDetails(String orderNo, Long memberId) {
        List<Order> orders = em.createQuery("select distinct o" +
                                " from Order o" +
                                " join fetch o.orderItems" +
                                " left join fetch o.release" +
                                " where o.orderNo = :orderNo" +
                                " and o.memberId = :memberId",
                        Order.class)
                .setParameter("orderNo", orderNo)
                .setParameter("memberId", memberId)
                .getResultList();

        Order order = orders.get(0);

        OrderRelease release = order.getRelease();
        ReleaseStatus releaseStatus = null;

        if (release != null) {
            releaseStatus = release.getStatus();
        }

        return new MyOrderFetch(order, releaseStatus);
    }

    /**
     * 레코드화
     * @param order
     * @param releaseStatus
     */
    private record MyOrderFetch(Order order, ReleaseStatus releaseStatus) {
    }

}
