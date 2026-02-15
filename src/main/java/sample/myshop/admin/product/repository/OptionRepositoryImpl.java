package sample.myshop.admin.product.repository;

import jakarta.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.product.domain.Option;
import sample.myshop.admin.product.domain.OptionValue;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OptionRepositoryImpl implements OptionRepository {

    private final EntityManager em;

    @Override
    public List<Option> findByProductId(Long productId) {

        return em.createQuery("select o from Option o where o.product.id = :productId order by o.sortOrder asc", Option.class)
                .setParameter("productId", productId)
                .getResultList();
    }

    @Override
    public boolean checkDuplicate(Long productId, String nameTrim) {
        List<Option> optionList = em.createQuery("select o from Option o where o.product.id = :productId and o.name = :nameTrim", Option.class)
                .setParameter("productId", productId)
                .setParameter("nameTrim", nameTrim)
                .getResultList();

        return !optionList.isEmpty();
    }

    @Override
    public Integer getOptionMaxSortOrder(Long productId) {
        return em.createQuery("select max(o.sortOrder) from Option o where o.product.id = :productId", Integer.class)
                .setParameter("productId", productId)
                .getSingleResult();
    }

    @Override
    public void save(Option option) {
        em.persist(option);
    }

    @Override
    public Option findById(Long optionId) {
        return em.find(Option.class, optionId);
    }

    @Override
    public Integer getOptionValueMaxOrder(Long optionId) {
        return em.createQuery("select max(ov.sortOrder) from OptionValue ov where ov.option.id = :optionId", Integer.class)
                .setParameter("optionId", optionId)
                .getSingleResult();
    }

    @Override
    public void saveOptionValue(OptionValue optionValue) {
        em.persist(optionValue);
    }

    @Override
    public List<OptionValue> findByOptionIds(List<Long> optionIds) {
        return em.createQuery("select ov" +
                " from OptionValue ov" +
                " join fetch ov.option o" +
                " where ov.option.id in :optionIds" +
                " order by ov.sortOrder asc, ov.id asc",
                OptionValue.class)
                .setParameter("optionIds", optionIds)
                .getResultList();
    }
}
