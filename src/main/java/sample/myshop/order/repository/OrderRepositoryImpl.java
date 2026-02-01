package sample.myshop.order.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
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
    public DefaultVariantSnapshotDto findDefaultVariant(Long productId) {
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
    public Inventory findInventoryForUpdateByVariantId(Long variantId) {
        try {
            return em.createQuery("select i from Inventory i where i.variant.id = :variantId", Inventory.class)
                    .setParameter("variantId", variantId)
                    /**
                     * jpql로 조회 할때 DB에 락을 거는것
                     * 현재 트랜잭션이 끝날때 까지 유지 (commit or rollback)
                     */
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException("재고를 찾을 수 없습니다.: " + variantId);
        }
    }

    @Override
    public void save(Order order) {
        em.persist(order);
    }
}
