package sample.myshop.admin.product.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.admin.product.domain.Variant;
import sample.myshop.admin.product.domain.dto.web.ProductCreateDto;
import sample.myshop.enums.product.Currency;
import sample.myshop.enums.product.SaleStatus;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceImplTest {

    @Autowired
    private ProductService productService;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("상품 생성 시 DEFAULT Variant 1개 + Inventory(0) 생성")
    void createProduct_createsDefaultVariantAndInventory() {
        // given (DTO는 class로)
        ProductCreateDto req = ProductCreateDto.of(
                "TEST_CODE",
                "TEST_NAME",
                "apple-iphone-15-pro-max",
                "testDescription",
                SaleStatus.ACTIVE,
                50000,
                Currency.KRW
        );
        // when
        Long productId = productService.createProduct(req);

        // then
        assertThat(productId).isNotNull();

        em.flush();
        em.clear();

        // 1) 해당 Product에 속한 DEFAULT Variant가 정확히 1개
        Long defaultCount = em.createQuery(
                        "select count(v) " +
                                "from Variant v " +
                                "where v.product.id = :productId and v.isDefault = true",
                        Long.class
                )
                .setParameter("productId", productId)
                .getSingleResult();

        assertThat(defaultCount).isEqualTo(1L);

        // 2) 그 DEFAULT Variant 조회
        Variant defaultVariant = em.createQuery(
                        "select v " +
                                "from Variant v " +
                                "where v.product.id = :productId and v.isDefault = true",
                        Variant.class
                )
                .setParameter("productId", productId)
                .getSingleResult();

        assertThat(defaultVariant.getId()).isNotNull();

        // 3) Inventory 존재 + 수량 0 검증
        Inventory inv = em.find(Inventory.class, defaultVariant.getId()); // Inventory PK = variant_id (@MapsId)
        assertThat(inv).isNotNull();

        assertThat(inv.getStockQuantity()).isZero();
        assertThat(inv.getReservedQuantity()).isZero();
        assertThat(inv.getSafetyStock()).isZero();
    }
}