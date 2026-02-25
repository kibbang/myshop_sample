package sample.myshop.order.facade;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.admin.product.domain.Product;
import sample.myshop.admin.product.repository.ProductRepository;
import sample.myshop.enums.product.Currency;
import sample.myshop.enums.product.SaleStatus;
import sample.myshop.member.domain.Member;
import sample.myshop.member.repository.MemberRepository;
import sample.myshop.member.service.MemberService;
import sample.myshop.member.domain.dto.MemberRegisterFormDto;
import sample.myshop.order.session.OrderDeliveryRequestDto;
import sample.myshop.order.session.OrderPrepareLineSession;
import sample.myshop.order.session.OrderPrepareSession;
import sample.myshop.shop.my.cart.service.CartService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderCheckoutFacadeRollbackIntegrationTest {

    @Autowired
    private OrderCheckoutFacade orderCheckoutFacade;

    @Autowired
    private EntityManager em;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Test
    @Rollback(false)
    @Transactional
    void 카트삭제가_실패하면_트랜잭션은_롤백대상으로_마킹된다() {
        // given: 회원 생성
        String buyerLoginId = "buyer-" + System.nanoTime();
        Long memberId = ensureMember(buyerLoginId);

        // given: 상품 생성 + 재고 10 세팅
        Long productId = createProductAndSetStock(10);
        Long variantId = findVariantIdByProductId(productId);

        // given: 카트 주문 세션 구성
        int orderQty = 2;
        OrderPrepareLineSession line = OrderPrepareLineSession.of(productId, variantId, orderQty);
        OrderPrepareSession prepareSession = OrderPrepareSession.fromCart(
                List.of(line),
                List.of(1000L)
        );

        OrderDeliveryRequestDto deliveryForm = new OrderDeliveryRequestDto();
        deliveryForm.setReceiverName("tester");
        deliveryForm.setReceiverPhone("01012345678");
        deliveryForm.setReceiverZipcode("12345");
        deliveryForm.setReceiverBaseAddress("기본주소");
        deliveryForm.setReceiverDetailAddress("상세주소");
        deliveryForm.setDeliveryMemo("메모");

        assertTrue(TestTransaction.isActive(), "테스트 트랜잭션이 활성 상태여야 합니다.");
        assertFalse(TestTransaction.isFlaggedForRollback(), "초기에는 rollback-only가 아니어야 의미있는 테스트입니다.");

        // when
        assertThrows(RuntimeException.class, () -> orderCheckoutFacade.checkoutFromPrepare(
                prepareSession, memberId, buyerLoginId, deliveryForm
        ));

        // then
        assertThrows(UnexpectedRollbackException.class, TestTransaction::end);
    }

    private Long ensureMember(String loginId) {
        Member found = memberRepository.findByLoginId(loginId);
        if (found != null) return found.getId();

        MemberRegisterFormDto form = new MemberRegisterFormDto();
        form.setLoginId(loginId);
        form.setPassword("pw-" + System.nanoTime());
        form.setName("테스터");
        form.setPhone("01012345678");
        form.setZipcode("123-123");
        form.setBaseAddress("성남시");
        form.setDetailAddress("분당구");

        memberService.join(form);

        Member created = memberRepository.findByLoginId(loginId);
        if (created == null) throw new IllegalStateException("테스트 회원 생성 실패");
        return created.getId();
    }

    private Long createProductAndSetStock(int stockQty) {
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
        inventory.updateStockQuantity(stockQty);

        em.flush();
        em.clear();

        return productId;
    }

    private Long findVariantIdByProductId(Long productId) {
        return em.createQuery(
                        "select i.variant.id from Inventory i where i.variant.product.id = :productId",
                        Long.class
                )
                .setParameter("productId", productId)
                .getSingleResult();
    }

    private int findInventoryStockByVariantId(Long variantId) {
        return em.createQuery(
                        "select i.stockQuantity from Inventory i where i.variant.id = :variantId",
                        Integer.class
                )
                .setParameter("variantId", variantId)
                .getSingleResult();
    }

    private long countOrdersByBuyerLoginId(String buyerLoginId) {
        return em.createQuery(
                        "select count(o) from Order o where o.buyerLoginId = :buyerLoginId",
                        Long.class
                )
                .setParameter("buyerLoginId", buyerLoginId)
                .getSingleResult();
    }

    @TestConfiguration
    static class FailCartServiceConfig {
        @Bean
        @Primary
        public CartService cartService() {
            return new CartService() {
                // 필요한 메서드만 실패시키고, 나머지는 호출되지 않도록(호출되면 바로 알 수 있게) 처리
                @Override
                public void removeItems(Long memberId, List<Long> cartItemIds) {
                    throw new RuntimeException("카트 삭제 실패(통합테스트용)");
                }

                @Override
                public void addItem(jakarta.servlet.http.HttpSession session, sample.myshop.auth.SessionUser loginUser, sample.myshop.shop.my.cart.domain.dto.CartAddRequestDto request) {
                    throw new UnsupportedOperationException("not used");
                }

                @Override
                public java.util.List<sample.myshop.shop.my.cart.domain.CartItem> getCartItems(jakarta.servlet.http.HttpSession session, sample.myshop.auth.SessionUser loginUser) {
                    throw new UnsupportedOperationException("not used");
                }

                @Override
                public void changeQuantity(jakarta.servlet.http.HttpSession session, sample.myshop.auth.SessionUser loginUser, Long cartItemId, sample.myshop.shop.my.cart.domain.dto.CartChangeQuantityRequestDto req) {
                    throw new UnsupportedOperationException("not used");
                }

                @Override
                public void removeItem(jakarta.servlet.http.HttpSession session, sample.myshop.auth.SessionUser loginUser, Long cartItemId) {
                    throw new UnsupportedOperationException("not used");
                }

                @Override
                public sample.myshop.shop.my.cart.domain.dto.CartListDto getCartView(jakarta.servlet.http.HttpSession session, sample.myshop.auth.SessionUser loginUser) {
                    throw new UnsupportedOperationException("not used");
                }

                @Override
                public java.util.List<sample.myshop.shop.my.cart.domain.dto.CartItemOrderSourceDto> getSelectedCartItemsForOrder(Long memberId, java.util.List<Long> selectedCartItemIds) {
                    throw new UnsupportedOperationException("not used");
                }

                @Override
                public java.util.List<sample.myshop.shop.my.cart.domain.dto.CartItemOrderSourceDto> getAllCartItemsForOrder(Long memberId) {
                    throw new UnsupportedOperationException("not used");
                }
            };
        }
    }
}