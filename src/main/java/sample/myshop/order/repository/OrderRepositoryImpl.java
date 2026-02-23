package sample.myshop.order.repository;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.order.domain.dto.web.OrderListItemDto;
import sample.myshop.admin.order.domain.dto.web.OrderSearchConditionDto;
import sample.myshop.common.exception.NotFoundException;
import sample.myshop.enums.order.OrderStatus;
import sample.myshop.order.domain.Order;
import sample.myshop.order.domain.dto.VariantSnapshotDto;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final EntityManager em;

    @Override
    public VariantSnapshotDto findVariantForOrder(Long productId, Long variantId) {
        VariantSnapshotDto foundVariant = em.createQuery("select new sample.myshop.order.domain.dto.VariantSnapshotDto(v.id, v.sku, v.salePrice, p.name, p.basePrice)" +
                " from Variant v" +
                " join v.product p" +
                " where p.id = :productId" +
                " and  v.id = :variantId", VariantSnapshotDto.class
        )
                .setParameter("productId", productId)
                .setParameter("variantId", variantId)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (foundVariant == null) {
            throw new NotFoundException("옵션조합을 찾을 수 없습니다.: " + productId);
        }

        return foundVariant;
    }

    @Override
    public void save(Order order) {
        em.persist(order);
    }

    @Override
    public Order findByIdWithDetail(Long orderId) {
        try {
            return em.createQuery("select distinct o" +
                            " from Order o" +
                            " left join fetch o.release r" + // left 추가 > release 추가 전 생성된 주문들 타겟
                            " join fetch o.orderItems oi" +
                            " where o.id = :orderId",
                            Order.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();
        } catch (Exception e) {
            throw new EntityNotFoundException("주문을 찾을 수 없습니다.: " + orderId);
        }
    }

    @Override
    public List<OrderListItemDto> findOrders(OrderSearchConditionDto condition, int page, int size) {
        String keyword = condition.getKeyword();
        OrderStatus status = condition.getStatus();

        String baseJpql = "select new sample.myshop.admin.order.domain.dto.web.OrderListItemDto(o.id, o.orderNo, o.buyerLoginId, o.status, o.totalAmount, o.createdAt)" +
                " from Order o";

        boolean isFirstCondition = true;

        if (keyword != null && !keyword.isBlank()) {
            if (isFirstCondition) {
                baseJpql += " where o.orderNo like :keyword or o.buyerLoginId like :keyword";
                isFirstCondition = false;
            } else {
                baseJpql += " and (o.orderNo like :keyword or o.buyerLoginId like :keyword)";
            }
        }

        if (status != null) {
            if (isFirstCondition) {
                baseJpql += " where o.status = :status";
                isFirstCondition = false;
            } else {
                baseJpql += " and o.status = :status";
            }
        }

        baseJpql += " order by o.id desc";

        TypedQuery<OrderListItemDto> orderListItemDtoTypedQuery = em.createQuery(baseJpql, OrderListItemDto.class).setFirstResult((page - 1) * size).setMaxResults(size);

        if (keyword != null && !keyword.isBlank()) {
            orderListItemDtoTypedQuery.setParameter("keyword", "%" + keyword + "%");
        }

        if (status != null) {
            orderListItemDtoTypedQuery.setParameter("status", status);
        }

        return orderListItemDtoTypedQuery.getResultList();
    }

    @Override
    public Long countOrders(OrderSearchConditionDto condition) {
        return em.createQuery("select count(o) from Order o", Long.class).getSingleResult();
    }

    @Override
    public Order findByOrderNoWithOrderItems(String orderNo) {
        return em.createQuery("select distinct o from Order o join fetch o.orderItems where o.orderNo = :orderNo", Order.class)
                .setParameter("orderNo", orderNo)
                .getSingleResult();
    }
}
