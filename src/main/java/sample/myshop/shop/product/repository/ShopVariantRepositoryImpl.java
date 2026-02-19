package sample.myshop.shop.product.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.product.domain.Variant;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ShopVariantRepositoryImpl implements ShopVariantRepository {

    private final EntityManager em;

    @Override
    public Variant findById(Long variantId) {
        return em.find(Variant.class, variantId);
    }

    @Override
    public Long findDefaultVariantIdByProductId(Long productId) {
        List<Long> result = em.createQuery(
                        "select v.id " +
                                "from Variant v " +
                                "where v.product.id = :productId " +
                                "  and v.isDefault = true",
                        Long.class
                )
                .setParameter("productId", productId)
                .setMaxResults(1)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }
}
