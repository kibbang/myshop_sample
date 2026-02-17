package sample.myshop.admin.product.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.myshop.enums.product.SaleStatus;
import sample.myshop.common.entity.CommonEntity;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static sample.myshop.enums.product.SaleStatus.*;

/**
 * 같은 상품이라도 옵션 조합별로 실제로 파는 단위
*/
@Entity
@Getter
@Table(name = "prd_variants")
@NoArgsConstructor
public class Variant extends CommonEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true)
    private String sku;

    @Enumerated(EnumType.STRING)
    private SaleStatus status;

    @Column(name = "is_default")
    private boolean isDefault;

    private Integer price;

    @Column(name = "sale_price")
    private Integer salePrice;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(fetch = LAZY, mappedBy = "variant")
    private Inventory inventory;


    private Variant(Product product, String sku, Integer price) {
        this.sku = sku;
        this.status = ACTIVE;
        this.isDefault = false;
        this.price = price;
        this.salePrice = null;
        this.product = product;
    }

    /**
     * 기본 판매 상품 단위 생성
     * @param product
     * @return
     */
    public static Variant createDefault(Product product) {
        Variant variant = new Variant();

        variant.product = product;
        variant.isDefault = true;
        variant.sku = makeDefaultSku(product);
        variant.status = ACTIVE;
        variant.price = null;
        variant.salePrice = null;

        return variant;
    }

    public static Variant create(Product product, String sku, Integer price) {
        return new Variant(product, sku, price);
    }

    /**
     * 기본 판매단위 sku 생성
     * @param product
     * @return
     */
    private static String makeDefaultSku(Product product) {
        return product.getCode() + "-DEFAULT";
    }
}
