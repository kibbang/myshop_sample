package sample.myshop.shop.product.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.product.domain.Option;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ShopProductOptionRepositoryImpl implements ShopProductOptionRepository {

    private final EntityManager em;

    @Override
    public List<Option> findByProductId(Long productId) {
        return em.createQuery("select o from Option o  where o.product.id = :productId", Option.class)
                .setParameter("productId", productId)
                .getResultList();
    }
}
