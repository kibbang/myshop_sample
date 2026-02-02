package sample.myshop;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.admin.product.domain.Inventory;
import sample.myshop.admin.product.domain.Product;
import sample.myshop.admin.product.repository.ProductRepository;
import sample.myshop.enums.product.Currency;
import sample.myshop.enums.product.SaleStatus;
import sample.myshop.order.service.OrderService;

@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalDataInitializer implements CommandLineRunner { // SEED 역할이군

    private final ProductRepository productRepository;
    private final OrderService orderService;
    private final EntityManager em;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 주문 한건만
        Long count = em.createQuery("select count(o) from Order o", Long.class)
                .getSingleResult();
        if (count > 0) return;

        // 1) 상품 생성 + 저장 (DEFAULT Variant/Inventory 자동 생성)
        Product product = Product.createProduct(
                "P-SEED-" + System.nanoTime(),
                "시드 상품",
                "seed-product-" + System.nanoTime(),
                "목록 화면 확인용 상품",
                SaleStatus.ACTIVE,
                10_000,
                Currency.KRW
        );

        Long productId = productRepository.save(product);

        // 2) 재고 채우기
        Inventory inv = productRepository.findProductByIdForUpdateInventoryStock(productId);
        inv.updateStockQuantity(10);

        // 3) 주문 1건 생성
        orderService.placeOrder(productId, 2, "buyer01");
    }
}
