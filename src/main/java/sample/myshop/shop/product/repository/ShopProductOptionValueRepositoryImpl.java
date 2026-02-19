package sample.myshop.shop.product.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.product.domain.OptionValue;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ShopProductOptionValueRepositoryImpl implements ShopProductOptionValueRepository {

    private final EntityManager em;

    @Override
    public List<OptionValue> findByOptionId(List<Long> optionIds) {
        return em.createQuery("select ov from OptionValue ov where ov.option.id in (:optionIds) order by ov.option.id, ov.sortOrder asc", OptionValue.class)
                .setParameter("optionIds", optionIds)
                .getResultList();
    }
}
