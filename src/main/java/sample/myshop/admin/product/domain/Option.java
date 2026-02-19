package sample.myshop.admin.product.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;

@Entity
@Getter
@Table(name = "prd_options")
@NoArgsConstructor
public class Option {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;
    private int sortOrder;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Option(String name, int sortOrder, Product product) {
        this.name = name;
        this.sortOrder = sortOrder;
        this.product = product;
    }

    /**
     * 생성 메소드
     * @param name
     * @param sortOrder
     * @param product
     * @return
     */
    public static Option createOption(String name, int sortOrder, Product product) {
        return new Option(name, sortOrder, product);
    }
}
