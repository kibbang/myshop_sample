package sample.myshop.shop.product.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.product.domain.Option;
import sample.myshop.admin.product.domain.Variant;
import sample.myshop.enums.product.SaleStatus;
import sample.myshop.shop.main.dto.MainProductCardDto;
import sample.myshop.shop.product.domain.dto.web.ShopProductDetailDto;
import sample.myshop.shop.product.domain.dto.web.ShopProductListItemDto;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ShopProductRepositoryImpl implements ShopProductRepository {

    private final EntityManager em;

    @Override
    public List<ShopProductListItemDto> findAll() {
        return em.createQuery("select new sample.myshop.shop.product.domain.dto.web.ShopProductListItemDto(p.id, p.name, p.basePrice, p.currency)" +
                " from Product p" +
                " where p.status = :status order by p.id desc", ShopProductListItemDto.class)
                    .setParameter("status", SaleStatus.ACTIVE).getResultList();
    }

    @Override
    public ShopProductDetailDto findById(Long productId) {
        return em.createQuery("select new sample.myshop.shop.product.domain.dto.web.ShopProductDetailDto(p.id, p.name, p.description, p.basePrice, p.currency)" +
                " from Product p where p.id = :productId and p.status = :status", ShopProductDetailDto.class)
                .setParameter("productId", productId)
                .setParameter("status", SaleStatus.ACTIVE)
                .getSingleResult();
    }

    @Override
    public List<MainProductCardDto> findMainProductCards(int limit) {
        return em.createQuery("select new sample.myshop.shop.main.dto.MainProductCardDto(p.id, p.name, p.slug, coalesce(v.salePrice, p.basePrice) , p.currency, v.sku, i.stockQuantity)" +
                " from Product p" +
                " join Variant v on v.product = p" +
                " join Inventory i on i.variant = v" +
                " where p.status = :status" +
                " and v.isDefault = true" +
                " order by p.id desc", MainProductCardDto.class)
                .setParameter("status", SaleStatus.ACTIVE)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public Long findMatchedVariant(Long productId, List<Long> optionValueIds) {

        if (optionValueIds == null || optionValueIds.isEmpty()) {
            return null;
        }
            List<Long> distinctIds = optionValueIds.stream().distinct().toList();
        long size = distinctIds.size();

        List<Long> resultList = em.createQuery("select v.id from Variant v where v.product.id = :productId" +
                        " and v.isDefault = false" +
                        " and (select count(vov) from VariantOptionValue vov where vov.variant = v and vov.optionValue.id in :optionValueIds) = :size" +
                        " and (select count(vov) from VariantOptionValue vov where vov.variant = v) = :size", Long.class)
                .setParameter("productId", productId)
                .setParameter("optionValueIds", distinctIds)
                .setParameter("size", size)
                .setMaxResults(1)
                .getResultList();

        return resultList.isEmpty()
                ? null
                : resultList.get(0);
    }
}
