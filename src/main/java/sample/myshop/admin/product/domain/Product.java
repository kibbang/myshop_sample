package sample.myshop.admin.product.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.myshop.enums.product.Currency;
import sample.myshop.enums.product.SaleStatus;
import sample.myshop.common.entity.CommonEntity;

import static jakarta.persistence.GenerationType.*;


/**
 * 상품 페이지에 보여줄 대표 정보(상품코드, 이름, 설명, 기본가격, 상태 등)
 */

@Entity
@Getter
@Table(name = "prd_products")
@NoArgsConstructor
public class Product extends CommonEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, name = "product_code", length = 50)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus status;

    @Column(nullable = false, name = "base_price")
    private int basePrice;

    @Column(nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    private Product(String code, String name, String slug, String description, SaleStatus status, int basePrice, Currency currency) {
        this.code = code;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.status = status;
        this.basePrice = basePrice;
        this.currency = currency;
    }

    /**
     * 생성 메소드
     * @param code
     * @param name
     * @param slug
     * @param description
     * @param status
     * @param basePrice
     * @param currency
     * @return Product
     */
    public static Product createProduct(String code,
                                        String name,
                                        String slug,
                                        String description,
                                        SaleStatus status,
                                        int basePrice,
                                        Currency currency
    ) {
        return new Product(code, name, slug, description, status, basePrice, currency);
    }

    /**
     * 수정
     * @param name
     * @param slug
     * @param description
     * @param status
     * @param basePrice
     * @param currency
     */
    public void updateBasicInfo(String name, String slug, String description, SaleStatus status, int basePrice, Currency currency) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.status = status;
        this.basePrice = basePrice;
        this.currency = currency;
    }
}

