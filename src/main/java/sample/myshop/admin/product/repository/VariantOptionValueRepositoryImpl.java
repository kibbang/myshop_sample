package sample.myshop.admin.product.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.product.domain.VariantOptionValue;

@Repository
@RequiredArgsConstructor
public class VariantOptionValueRepositoryImpl implements VariantOptionValueRepository{

    private final EntityManager em;

    @Override
    public void save(VariantOptionValue variantOptionValue) {
        em.persist(variantOptionValue);
    }
}
