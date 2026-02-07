package sample.myshop.admin.release.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.release.domain.OrderRelease;

@Repository
@RequiredArgsConstructor
public class ReleaseRepositoryImpl implements ReleaseRepository {

    private final EntityManager em;

    @Override
    public OrderRelease findByOrderId(Long orderId) {
        return  em.createQuery("select r from OrderRelease r join fetch r.order o where o.id = :orderId",
                OrderRelease.class)
                .setParameter("orderId", orderId)
                .getSingleResult();
    }
}
