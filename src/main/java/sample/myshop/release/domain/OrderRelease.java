package sample.myshop.release.domain;

import jakarta.persistence.*;
import lombok.Getter;

import lombok.NoArgsConstructor;
import sample.myshop.common.entity.CommonEntity;
import sample.myshop.order.domain.Order;
import sample.myshop.release.enums.ReleaseStatus;

import static jakarta.persistence.GenerationType.*;
import static sample.myshop.release.enums.ReleaseStatus.*;


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
    private ReleaseStatus status = READY;

    public static OrderRelease create() {
        return new OrderRelease();
    }

    public void assignOrder(Order order) {
        this.order = order;
    }

    /**
     * 출고 상태로 변경
     */
    public void toRelease() {
        statusCheck(RELEASED);

        status = RELEASED;
    }

    /**
     * 취소
     */
    public void toCancel() {
        statusCheck(CANCELED);

        status = CANCELED;
    }

    /**
     * 상태 변경 이전 변경 가능 여부 체크
     *
     * @param destinationStatus
     */
    private void statusCheck(ReleaseStatus destinationStatus) {

        // 애초에 목적 상태값이 넘어오지 않으면 오류지
        if (destinationStatus == null) {
            throw new IllegalArgumentException("변경할 상태가 유효하지 않습니다.");
        }

        // 출고 상태로 가거나 취소로 가는데
        if (destinationStatus == RELEASED || destinationStatus == CANCELED) {
            // 현재 상태가 준비가 아니면 오류
            if (status != READY) {
                throw new IllegalStateException("출고 상태가 '" + READY.getLabel() + "'이어야 합니다.");
            }
        }
    }
}
