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

    @Override
    public Long countOption(Long productId) {
        return em.createQuery("select count(o) from Option o where o.product.id = :productId", Long.class)
                .setParameter("productId", productId)
                .getSingleResult();
    }

    @Override
    public List<OptionValue> validateOptionFromProduct(Long productId, List<Long> optionValueIds) {
        return em.createQuery("select ov from OptionValue ov join fetch ov.option o join fetch o.product p where ov.id in :optionValueIds and p.id = :proudtId", OptionValue.class)
                .setParameter("optionValueIds", optionValueIds)
                .setParameter("proudtId", productId)
                .getResultList();

    }

    @Override
    public boolean checkOptionExists(Long productId, List<Long> normalizedOptionValueIds) {

        int size = normalizedOptionValueIds.size();

        List<Long> resultList = em.createQuery("select v.id" +
                        " from VariantOptionValue vov" +
                        " join vov.variant v" +
                        " where v.product.id = :productId" +
                        " group by v.id" +
                        " having count(vov.id) = :size" +
                        " and sum(case when vov.optionValue.id in :normalizedOptionValueIds then 1 else 0 end) = :size", Long.class)
                .setParameter("productId", productId)
                .setParameter("size", size)
                .setParameter("normalizedOptionValueIds", normalizedOptionValueIds)
                .setMaxResults(1)
                .getResultList();

        return !resultList.isEmpty();
    }

    @Override
    public List<OptionValue> findAllOptionValuesByIds(List<Long> optionValueIds) {
        if (optionValueIds == null || optionValueIds.isEmpty()) {
            return List.of();
        }

        return em.createQuery("select ov from OptionValue ov where ov.id in :optionValueIds", OptionValue.class)
                .setParameter("optionValueIds", optionValueIds)
                .getResultList();
    }
}
