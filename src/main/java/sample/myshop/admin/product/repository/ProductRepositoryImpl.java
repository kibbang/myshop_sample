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
import sample.myshop.admin.product.domain.dto.web.ProductDetailDto;
import sample.myshop.admin.product.domain.dto.web.ProductListItemDto;
import sample.myshop.admin.product.domain.dto.web.ProductSearchConditionDto;
import sample.myshop.admin.product.domain.dto.web.SkuStockRowDto;
import sample.myshop.enums.product.SaleStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        String baseJpql = "select p from Product p ";
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

        Map<Long, SkuStockRowDto> productSkuAndStock = findSkuAndStockByProductIds(productList.stream().map(Product::getId).toList());


        List<ProductListItemDto> productDtoList = new ArrayList<>();

        for (Product product : productList) {
            // 일단 기본을 만듬
            ProductListItemDto productListItemDto = ProductListItemDto.of(
                    product.getId(),
                    product.getCode(),
                    product.getName(),
                    product.getStatus(),
                    product.getBasePrice(),
                    product.getCurrency(),
                    product.getCreatedAt()
            );

            // 각 상품 별 id로 컬렉션 조회
            SkuStockRowDto skuStockRowDto = productSkuAndStock.get(product.getId());

            if (skuStockRowDto != null) {
                productListItemDto.addSkuAndStock(skuStockRowDto.getSku(), skuStockRowDto.getStockQuantity());
            }

            productDtoList.add(productListItemDto);
        }

        return productDtoList;
    }

    @Override
    public Long countProducts(ProductSearchConditionDto condition) {
        return em.createQuery("select count(p.id) from Product p", Long.class).getSingleResult();
    }

    @Override
    public ProductDetailDto findProductById(Long productId) {

        Inventory inventory = em.createQuery("select i" +
                " from Inventory i" +
                " join fetch i.variant v" +
                " join fetch v.product p " +
                "where p.id = :productId and v.isDefault = true", Inventory.class)
                .setParameter("productId", productId)
                .getSingleResult();

        if (inventory == null) {
           return null;
        }

        Variant variant = inventory.getVariant();
        Product product = variant.getProduct();

        return ProductDetailDto.of(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getStatus(),
                product.getBasePrice(),
                product.getCurrency(),
                product.getSlug(),
                product.getDescription(),
                variant.getSku(),
                inventory.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    @Override
    public Product findProductByIdForUpdate(Long productId) {
        return em.find(Product.class, productId);
    }

    @Override
    public Inventory findProductByIdForUpdateInventoryStock(Long productId) {
        return em.createQuery("select i " +
                        " from Inventory i " +
                        " join fetch i.variant v " +
                        " join fetch v.product p " +
                        " where p.id = :productId and v.isDefault = true", Inventory.class
                )
                .setParameter("productId", productId)
                .getSingleResult();
    }

    private Map<Long, SkuStockRowDto> findSkuAndStockByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<SkuStockRowDto> skuStockRows = em.createQuery("select new sample.myshop.admin.product.domain.dto.web.SkuStockRowDto(p.id, v.sku, i.stockQuantity)" +
                        " from Variant v" +
                        " join v.product p join Inventory i on i.variant = v" +
                        " where p.id in (:productIds) and v.isDefault = true", SkuStockRowDto.class)
                .setParameter("productIds", productIds)
                .getResultList();

        return skuStockRows.stream().collect(Collectors.toMap(SkuStockRowDto::getProductId, Function.identity(),(a, b) -> a));
    }
}
