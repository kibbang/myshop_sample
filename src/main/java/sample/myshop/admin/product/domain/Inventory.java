package sample.myshop.admin.product.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "prd_inventories")
public class Inventory {
    @Id
    @Column(name = "variant_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "variant_id")
    private Variant variant;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;
    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;
    @Column(name = "safety_stock", nullable = false)
    private int safetyStock;

    public static Inventory createZero(Variant variant) {
        Inventory inventory = new Inventory();
        inventory.variant = variant;
        inventory.stockQuantity = 0;
        inventory.reservedQuantity = 0;
        inventory.safetyStock = 0;
        return inventory;
    }

    public void updateStockQuantity(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("재고는 0보다 커야합니다.");
        }
        this.stockQuantity = value;
    }
}
