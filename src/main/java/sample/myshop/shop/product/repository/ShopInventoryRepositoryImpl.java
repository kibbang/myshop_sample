package sample.myshop.shop.product.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.product.domain.Inventory;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ShopInventoryRepositoryImpl implements ShopInventoryRepository {

    private final EntityManager em;

    @Override
    public Inventory findByVariantId(Long variantId) {
        List<Inventory> resultList = em.createQuery("select i from Inventory i where i.variant.id = :variantId", Inventory.class)
                .setParameter("variantId", variantId)
                .getResultList();

        return resultList.get(0);
    }
}
