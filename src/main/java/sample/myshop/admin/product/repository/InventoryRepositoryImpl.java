package sample.myshop.admin.product.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.product.domain.Inventory;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository{

    private final EntityManager em;
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
    public void save(Inventory inventory) {
        em.persist(inventory);
    }
}
