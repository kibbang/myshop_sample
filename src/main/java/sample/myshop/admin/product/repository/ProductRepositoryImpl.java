package sample.myshop.admin.product.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.admin.product.domain.Product;
import sample.myshop.admin.product.domain.Variant;
import sample.myshop.admin.product.domain.dto.web.ProductListItemDto;
import sample.myshop.admin.product.domain.dto.web.ProductSearchConditionDto;
import sample.myshop.admin.product.enums.SaleStatus;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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

    @Override
    public List<ProductListItemDto> findProducts(ProductSearchConditionDto condition, int page, int size) {
        String keyword = condition.getKeyword();
        SaleStatus status = condition.getStatus();

        // TODO: QueryDsl 배우면 개선 해야함 너무 불편...

        String baseJpql = "select p from Product p";
        boolean isFirstCondition = true;

        if (keyword != null && !keyword.isBlank()) {
            if (isFirstCondition) {
                baseJpql += " where p.name like :keyword";
                isFirstCondition = false;
            } else {
                baseJpql += " and p.name like :keyword";
            }
        }

        if (status != null) {
            if (isFirstCondition) {
                baseJpql += " where p.status = :status";
                isFirstCondition = false;
            } else {
                baseJpql += " and p.status = :status";
            }
        }

        baseJpql += " order by p.id desc";

        TypedQuery<Product> query = em.createQuery(baseJpql, Product.class).setFirstResult((page - 1) * size)
                .setMaxResults(size);

        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }

        if (status != null) {
            query.setParameter("status", status);
        }

        List<Product> productList = query.getResultList();


        List<ProductListItemDto> productDtoList = new ArrayList<>();

        for (Product product : productList) {
            productDtoList.add(ProductListItemDto.of(
                    product.getId(),
                    product.getCode(),
                    product.getName(),
                    product.getStatus(),
                    product.getBasePrice(),
                    product.getCurrency(),
                    product.getCreatedAt()
            ));
        }

        return productDtoList;
    }
}
