-- ord_releases (주문 출고/배송준비 상태)
DROP TABLE IF EXISTS ord_releases;

CREATE TABLE ord_releases (
                              id BIGINT NOT NULL AUTO_INCREMENT,

                              order_id BIGINT NOT NULL,
                              status VARCHAR(20) NOT NULL,          -- READY / RELEASED / CANCELED

                              created_at DATETIME(6) NOT NULL,
                              updated_at DATETIME(6) NOT NULL,
                              deleted_at DATETIME(6) NULL,

                              PRIMARY KEY (id),

    -- 주문 1건당 release 1건
                              CONSTRAINT uk_ord_releases_order_id UNIQUE (order_id),

    -- 조회/관리 편의 인덱스
                              INDEX idx_ord_releases_status (status),
                              INDEX idx_ord_releases_created_at (created_at),

    -- FK (ord_orders.id 참조)
                              CONSTRAINT fk_ord_releases_order_id
                                  FOREIGN KEY (order_id)
                                      REFERENCES ord_orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
