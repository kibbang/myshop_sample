package sample.myshop.admin.product.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.product.domain.Inventory;

import java.util.List;

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

    @Override
    public Inventory findForUpdateByProductIdAndVariantId(Long productId, Long variantId) {
        List<Inventory> resultList = em.createQuery("select i from Inventory i join i.variant v join v.product p where p.id = :productId and v.id = :variantId", Inventory.class)
                .setParameter("productId", productId)
                .setParameter("variantId", variantId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE) // 락 걸기
                .getResultList();

        if (resultList.isEmpty()) {
            throw new EntityNotFoundException("재로를 찾을 수 없습니다.");
        }

        return resultList.get(0);
    }

    @Override
    public Inventory findByVariantId(Long variantId) {
        return em.createQuery(
                        "select i " +
                                "from Inventory i " +
                                "where i.variant.id = :variantId",
                        Inventory.class
                )
                .setParameter("variantId", variantId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
