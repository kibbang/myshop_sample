package sample.myshop.release.service;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.admin.product.domain.Product;
import sample.myshop.admin.product.repository.ProductRepository;
import sample.myshop.admin.release.service.ReleaseServiceImpl;
import sample.myshop.enums.product.Currency;
import sample.myshop.enums.product.SaleStatus;
import sample.myshop.order.domain.Order;
import sample.myshop.order.repository.OrderRepository;
import sample.myshop.order.service.OrderService;
import sample.myshop.order.session.OrderDeliveryRequestDto;
import sample.myshop.release.enums.ReleaseStatus;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ReleaseServiceImplTest {

    @Autowired
    ReleaseServiceImpl releaseService;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository  orderRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("출고처리시 상태 정상전이")
    void release() {
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
        inventory.updateStockQuantity(100);

        em.flush();
        em.clear();

        OrderDeliveryRequestDto requestDto = new OrderDeliveryRequestDto();
        requestDto.setReceiverName("tester");
        requestDto.setReceiverPhone("01012345678");
        requestDto.setReceiverZipcode("1235468");
        requestDto.setReceiverBaseAddress("기본주소");
        requestDto.setReceiverDetailAddress("상세주소");
        requestDto.setDeliveryMemo("메모");

        String orderNo = orderService.placeOrder(productId, 2, "shoptester", requestDto);

        em.flush();
        em.clear();

        // when
        Order order = orderRepository.findByOrderNoWithOrderItems(orderNo);

        releaseService.toRelease(order.getId());

        em.flush();
        em.clear();

        // then
        order = orderRepository.findByOrderNoWithOrderItems(orderNo);
        Assertions.assertThat(order.getRelease().getStatus()).isEqualTo(ReleaseStatus.RELEASED);
    }

    @Test
    @DisplayName("출고완료일때 취소")
    void IllegalCancel() {
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
        inventory.updateStockQuantity(100);

        em.flush();
        em.clear();

        OrderDeliveryRequestDto requestDto = new OrderDeliveryRequestDto();
        requestDto.setReceiverName("tester");
        requestDto.setReceiverPhone("01012345678");
        requestDto.setReceiverZipcode("1235468");
        requestDto.setReceiverBaseAddress("기본주소");
        requestDto.setReceiverDetailAddress("상세주소");
        requestDto.setDeliveryMemo("메모");

        String orderNo = orderService.placeOrder(productId, 2, "shoptester", requestDto);

        em.flush();
        em.clear();

        Order order = orderRepository.findByOrderNoWithOrderItems(orderNo);

        releaseService.toRelease(order.getId());

        em.flush();
        em.clear();

        // when & then
        assertThrows(IllegalStateException.class, order::cancel);
    }
}
