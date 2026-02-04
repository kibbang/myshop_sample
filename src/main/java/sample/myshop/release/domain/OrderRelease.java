package sample.myshop.release.domain;

import jakarta.persistence.*;
import lombok.Getter;

import lombok.NoArgsConstructor;
import sample.myshop.common.entity.CommonEntity;
import sample.myshop.order.domain.Order;
import sample.myshop.release.enums.ReleaseStatus;

import static jakarta.persistence.GenerationType.*;


@Entity
@Getter
@Table(name = "ord_releases")
@NoArgsConstructor
public class OrderRelease extends CommonEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReleaseStatus status = ReleaseStatus.READY;

    public static OrderRelease create() {
        return new OrderRelease();
    }

    public void assignOrder(Order order) {
        this.order = order;
    }

    public void cancel() {
        if (this.order == null) {
            throw new IllegalStateException("해당하는 주문이 없습니다.");
        }

        if (status != ReleaseStatus.READY) {
            throw new IllegalArgumentException("주문 상태가 '" + ReleaseStatus.READY.getLabel() + "'이어야 합니다.");
        }

        status = ReleaseStatus.CANCELED;
    }
}
