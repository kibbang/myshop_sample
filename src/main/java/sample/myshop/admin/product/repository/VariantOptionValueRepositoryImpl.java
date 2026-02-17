package sample.myshop.admin.product.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.product.domain.VariantOptionValue;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class VariantOptionValueRepositoryImpl implements VariantOptionValueRepository{

    private final EntityManager em;

    @Override
    public void save(VariantOptionValue variantOptionValue) {
        em.persist(variantOptionValue);
    }

    @Override
    public List<VariantOptionValue> findOptionJoinsByVariantIds(List<Long> variantIds) {

        return em.createQuery("select vov" +
                " from VariantOptionValue vov" +
                " join fetch vov.optionValue ov" +
                " join fetch ov.option o" +
                " where vov.variant.id in :variantIds", VariantOptionValue.class)
                .setParameter("variantIds", variantIds)
                .getResultList();
    }
}
