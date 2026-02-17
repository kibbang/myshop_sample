package sample.myshop.admin.product.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.product.domain.Variant;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class VariantRepositoryImpl implements VariantRepository {

    private final EntityManager em;

    @Override
    public void save(Variant variant) {
        em.persist(variant);
    }

    @Override
    public List<Variant> findByProductId(Long productId) {
        return em.createQuery("select v" +
                " from Variant v" +
                " join Inventory i on v.id = i.variant.id" +
                " join fetch Product p on p.id = v.product.id" +
                " where p.id = :productId order by v.id desc", Variant.class)
                .setParameter("productId", productId)
                .getResultList();
    }
}
