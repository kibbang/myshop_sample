-- =========================================================
-- Orders (MySQL / Flyway) - Option A
-- PK(id) = BIGINT AUTO_INCREMENT
-- External order number(order_no) = CHAR(20) UNIQUE (YYYYMMDD + RANDOM12)
-- =========================================================

DROP TABLE IF EXISTS ord_order_items;
DROP TABLE IF EXISTS ord_orders;

-- -------------------------
-- ord_orders
-- -------------------------
CREATE TABLE ord_orders (
                            id              BIGINT          NOT NULL AUTO_INCREMENT,
                            order_no        CHAR(20)        NOT NULL,  -- YYYYMMDD(8) + RANDOM(12)
                            buyer_login_id  VARCHAR(50)     NOT NULL,
                            status          VARCHAR(20)     NOT NULL,  -- ORDERED / CANCELED
                            total_amount    INT             NOT NULL,

                            created_at      DATETIME(6)     NOT NULL,
                            updated_at      DATETIME(6)     NOT NULL,
                            deleted_at      DATETIME(6)     NULL,

                            PRIMARY KEY (id),
                            UNIQUE KEY uk_ord_orders_order_no (order_no),
                            KEY idx_ord_orders_buyer_login_id (buyer_login_id),
                            KEY idx_ord_orders_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- ord_order_items
-- -------------------------
CREATE TABLE ord_order_items (
                                 id                    BIGINT          NOT NULL AUTO_INCREMENT,
                                 order_id              BIGINT          NOT NULL,

                                 product_id            BIGINT UNSIGNED NOT NULL,
                                 variant_id            BIGINT UNSIGNED NOT NULL,

                                 sku_snapshot          VARCHAR(100)    NOT NULL,
                                 product_name_snapshot VARCHAR(255)    NOT NULL,

                                 unit_price            INT             NOT NULL,
                                 quantity              INT             NOT NULL,
                                 line_amount           INT             NOT NULL,

                                 created_at            DATETIME(6)     NOT NULL,
                                 updated_at            DATETIME(6)     NOT NULL,
                                 deleted_at            DATETIME(6)     NULL,

                                 PRIMARY KEY (id),
                                 KEY idx_ord_order_items_order_id (order_id),
                                 KEY idx_ord_order_items_product_id (product_id),
                                 KEY idx_ord_order_items_variant_id (variant_id),

                                 CONSTRAINT fk_ord_order_items_order
                                     FOREIGN KEY (order_id) REFERENCES ord_orders (id),

                                 CONSTRAINT fk_ord_order_items_product
                                     FOREIGN KEY (product_id) REFERENCES prd_products (id),

                                 CONSTRAINT fk_ord_order_items_variant
                                     FOREIGN KEY (variant_id) REFERENCES prd_variants (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
