package sample.myshop.order.repository;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.order.domain.Order;
import sample.myshop.order.domain.dto.DefaultVariantSnapshotDto;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final EntityManager em;

    @Override
    public DefaultVariantSnapshotDto findDefaultVariantForOrder(Long productId) {
        DefaultVariantSnapshotDto foundVariant = em.createQuery("select new sample.myshop.order.domain.dto.DefaultVariantSnapshotDto(v.id, v.sku, v.salePrice, p.name, p.basePrice)" +
                " from Variant v" +
                " join v.product p" +
                " where v.isDefault = true" +
                " and p.id = :productId", DefaultVariantSnapshotDto.class
        )
                .setParameter("productId", productId)
                .getSingleResult();

        if (foundVariant == null) {
            // TODO 커스텀 익셉션 생성 하면 교체
            throw new EntityNotFoundException("대표 Variant를 찾을 수 없습니다.: " + productId);
        }

        return foundVariant;
    }

    @Override
    public void save(Order order) {
        em.persist(order);
    }

    @Override
    public Order findByIdWithOrderItems(Long orderId) {
        try {
            return em.createQuery("select distinct o from Order o join fetch o.orderItems where o.id = :orderId", Order.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();
        } catch (Exception e) {
            throw new EntityNotFoundException("주문을 찾을 수 없습니다.: " + orderId);
        }
    }
}
