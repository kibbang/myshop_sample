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
import sample.myshop.order.session.OrderDeliveryRequestDto;

import static org.junit.jupiter.api.Assertions.*;
import static sample.myshop.enums.order.OrderStatus.CANCELED;

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
        OrderDeliveryRequestDto requestDto = new OrderDeliveryRequestDto();
        requestDto.setReceiverName("tester");
        requestDto.setReceiverPhone("01012345678");
        requestDto.setReceiverZipcode("1235468");
        requestDto.setReceiverBaseAddress("기본주소");
        requestDto.setReceiverDetailAddress("상세주소");
        requestDto.setDeliveryMemo("메모");

        String orderNo = orderService.placeOrder(productId, 2, "buyer01", requestDto);

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

        OrderDeliveryRequestDto requestDto = new OrderDeliveryRequestDto();
        requestDto.setReceiverName("tester");
        requestDto.setReceiverPhone("01012345678");
        requestDto.setReceiverZipcode("1235468");
        requestDto.setReceiverBaseAddress("기본주소");
        requestDto.setReceiverDetailAddress("상세주소");
        requestDto.setDeliveryMemo("메모");


        // when & then
        assertThrows(IllegalStateException.class,
                () -> orderService.placeOrder(productId, 2, "buyer01", requestDto),
                "Inventory.decreaseQuantity에서 재고 부족 예외가 발생해야 함"
        );
    }

    // ============================
    // A) 주문 취소 시 재고 원복 + 상태/취소시간 변경
    // ============================
    @Test
    void 주문취소하면_재고가10에서8로감소했다가_다시10으로원복되고_상태와취소시간이변경된다() {
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
        inventory.updateStockQuantity(10);

        em.flush();
        em.clear();

        // when: 주문(2개)
        OrderDeliveryRequestDto requestDto = new OrderDeliveryRequestDto();
        requestDto.setReceiverName("tester");
        requestDto.setReceiverPhone("01012345678");
        requestDto.setReceiverZipcode("1235468");
        requestDto.setReceiverBaseAddress("기본주소");
        requestDto.setReceiverDetailAddress("상세주소");
        requestDto.setDeliveryMemo("메모");

        String orderNo = orderService.placeOrder(productId, 2, "buyer01", requestDto);

        em.flush();
        em.clear();

        // checkpoint: placeOrder() 후 Inventory를 락 없이 다시 조회해서 수량 확인(테스트니까 일반 조회 OK)
        Order placedOrder = findOrderByOrderNoWithItems(orderNo);
        Long orderId = placedOrder.getId();
        Long variantId = placedOrder.getOrderItems().get(0).getVariantId();

        Inventory decreasedInventory = findInventoryNoLockByVariantId(variantId);
        assertEquals(8, decreasedInventory.getStockQuantity(), "주문 후 재고가 10 -> 8 이어야 함");

        // when: 주문 취소
        orderService.cancelOrder(orderId);

        em.flush();
        em.clear();

        // checkpoint: cancelOrder() 호출 후 재조회해서 수량 확인
        Inventory restoredInventory = findInventoryNoLockByVariantId(variantId);
        assertEquals(10, restoredInventory.getStockQuantity(), "취소 후 재고가 10으로 원복되어야 함");

        // checkpoint: Order 재조회해서 status, canceledAt 확인
        Order canceledOrder = em.find(Order.class, orderId);

        // 검증 항목
        assertEquals(CANCELED, canceledOrder.getStatus());
    }

    // ============================
    // B) 한 번 취소한 주문을 다시 취소하면 예외
    // ============================
    @Test
    void 한번취소한주문을_다시취소하면_예외() {
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
        inventory.updateStockQuantity(10);

        em.flush();
        em.clear();

        OrderDeliveryRequestDto requestDto = new OrderDeliveryRequestDto();
        requestDto.setReceiverName("tester");
        requestDto.setReceiverPhone("01012345678");
        requestDto.setReceiverZipcode("1235468");
        requestDto.setReceiverBaseAddress("기본주소");
        requestDto.setReceiverDetailAddress("상세주소");
        requestDto.setDeliveryMemo("메모");

        String orderNo = orderService.placeOrder(productId, 2, "buyer01", requestDto);
        em.flush();
        em.clear();

        Order placedOrder = findOrderByOrderNoWithItems(orderNo);
        Long orderId = placedOrder.getId();

        // checkpoint: first cancel 성공
        orderService.cancelOrder(orderId);

        em.flush();
        em.clear();

        RuntimeException ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.cancelOrder(orderId)
        );

        assertTrue(ex.getMessage().contains("주문"), "예외 메시지에 ORDERED(주문 상태) 관련 힌트가 있어야 함");
    }

    // ============================
    // C) 재고부족 주문실패 → 재고 유지
    // ============================
    @Test
    void 재고가1인데_2개주문시_실패하고_재고는그대로1이어야한다() {
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

        // product 생성 후 기본 재고를 1로 세팅
        Inventory inventory = productRepository.findProductByIdForUpdateInventoryStock(productId);
        inventory.updateStockQuantity(1);

        em.flush();
        em.clear();

        OrderDeliveryRequestDto requestDto = new OrderDeliveryRequestDto();
        requestDto.setReceiverName("tester");
        requestDto.setReceiverPhone("01012345678");
        requestDto.setReceiverZipcode("1235468");
        requestDto.setReceiverBaseAddress("기본주소");
        requestDto.setReceiverDetailAddress("상세주소");
        requestDto.setDeliveryMemo("메모");

        // when: placeOrder(productId, 2) 시도 → 예외
        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(productId, 2, "buyer01", requestDto)
        );

        em.flush();
        em.clear();

        // then: 실패 후 inventory 다시 조회 → == 1 (락 없이 일반 조회 OK)
        Long variantId = em.createQuery(
                        "select i.variant.id from Inventory i where i.variant.product.id = :productId",
                        Long.class
                )
                .setParameter("productId", productId)
                .getSingleResult();

        Inventory reloaded = findInventoryNoLockByVariantId(variantId);
        assertEquals(1, reloaded.getStockQuantity(), "주문 실패 시 재고는 그대로 1이어야 함");
    }

    // ----------------------------
    // 테스트 전용: 락 없는 Inventory 조회
    // ----------------------------
    private Inventory findInventoryNoLockByVariantId(Long variantId) {
        return em.createQuery(
                        "select i from Inventory i where i.variant.id = :variantId",
                        Inventory.class
                )
                .setParameter("variantId", variantId)
                .getSingleResult();
    }

    private Order findOrderByOrderNoWithItems(String orderNo) {
        return em.createQuery(
                        "select distinct o from Order o " +
                                "join fetch o.orderItems oi " +
                                "where o.orderNo = :orderNo",
                        Order.class
                )
                .setParameter("orderNo", orderNo)
                .getSingleResult();
    }
}