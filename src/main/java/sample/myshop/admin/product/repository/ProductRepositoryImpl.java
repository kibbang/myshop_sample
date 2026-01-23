package sample.myshop.admin.product.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.admin.product.domain.Product;
import sample.myshop.admin.product.domain.Variant;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Long save(Product product) {

        em.persist(product);

        Variant variant = Variant.createDefault(product);

        Inventory inventory = Inventory.createZero(variant);

        em.persist(variant);
        em.persist(inventory);

        return product.getId();
    }
}
