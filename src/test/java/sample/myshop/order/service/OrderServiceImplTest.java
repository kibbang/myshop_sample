package sample.myshop.order.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.admin.product.domain.Product;
import sample.myshop.admin.product.repository.ProductRepository;
import sample.myshop.enums.product.Currency;
import sample.myshop.enums.product.SaleStatus;
import sample.myshop.order.domain.Order;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceImplTest {
    @Autowired
    OrderService orderService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    EntityManager em;

    @Test
    void 주문성공() {
        // given: 상품 생성 + 저장(기본 Variant/Inventory 생성됨)
        Product product = Product.createProduct(
                "P-" + System.nanoTime(),
                "테스트 상품",
                "test-product-" + System.nanoTime(),
                "설명",
                SaleStatus.ACTIVE,
                10_000,
                Currency.KRW
        );

        Long productId = productRepository.save(product);

        Inventory inventory = productRepository.findProductByIdForUpdateInventoryStock(productId);
        inventory.updateStockQuantity(10);

        em.flush();
        em.clear();

        // when
        String orderNo = orderService.placeOrder(productId, 2, "buyer01");

        em.flush();
        em.clear();

        // then: 주문번호 반환 확인
        assertNotNull(orderNo);
        assertFalse(orderNo.isBlank());
        assertEquals(20, orderNo.length(), "OrderGenerator가 yyyyMMdd(8) + 12자리 = 20자리 주문번호를 생성");

        // then: 주문 저장/아이템 저장/금액 계산 확인
        Order savedOrder = em.createQuery(
                        "select distinct o from Order o " +
                                "join fetch o.orderItems oi " +
                                "where o.orderNo = :orderNo",
                        Order.class
                )
                .setParameter("orderNo", orderNo)
                .getSingleResult();

        assertEquals("buyer01", savedOrder.getBuyerLoginId());
        assertEquals(1, savedOrder.getOrderItems().size());

        // variant.salePrice가 null이면 basePrice를 unitPrice로 사용
        assertEquals(10_000 * 2, savedOrder.getTotalAmount());

        // then: 재고 차감 확인 (10 -> 8)
        Inventory decreasedInventory = productRepository.findProductByIdForUpdateInventoryStock(productId);
        assertEquals(8, decreasedInventory.getStockQuantity());
    }

    @Test
    void 재고부족이면_주문실패() {
        // given
        Product product = Product.createProduct(
                "P-" + System.nanoTime(),
                "테스트 상품",
                "test-product-" + System.nanoTime(),
                "설명",
                SaleStatus.ACTIVE,
                10_000,
                Currency.KRW
        );

        Long productId = productRepository.save(product);

        Inventory inventory = productRepository.findProductByIdForUpdateInventoryStock(productId);
        inventory.updateStockQuantity(1);

        em.flush();
        em.clear();

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(productId, 2, "buyer01"),
                "Inventory.decreaseQuantity에서 재고 부족 예외가 발생해야 함"
        );
    }
}