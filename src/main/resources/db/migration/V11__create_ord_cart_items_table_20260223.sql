DROP TABLE IF EXISTS ord_cart_items;

CREATE TABLE cart_items (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            session_id VARCHAR(128) NULL,
                            member_id BIGINT NULL,
                            product_id BIGINT UNSIGNED NOT NULL,
                            variant_id BIGINT UNSIGNED NOT NULL,
                            quantity INT NOT NULL,

                            created_at DATETIME NOT NULL,
                            updated_at DATETIME NOT NULL,
                            deleted_at DATETIME NULL,

                            PRIMARY KEY (id),

    -- 로그인 사용자 중복 방지 (같은 회원 + 같은 variant)
                            UNIQUE KEY uk_cart_items_member_variant (member_id, variant_id),

    -- 비로그인 사용자 중복 방지 (같은 세션 + 같은 variant)
                            UNIQUE KEY uk_cart_items_session_variant (session_id, variant_id),

    -- 조회 성능용 인덱스
                            KEY idx_cart_items_member_id (member_id),
                            KEY idx_cart_items_session_id (session_id),
                            KEY idx_cart_items_product_id (product_id),
                            KEY idx_cart_items_variant_id (variant_id),

    CONSTRAINT fk_cart_items_member FOREIGN KEY (member_id) REFERENCES mem_members(id),
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES prd_products(id),
    CONSTRAINT fk_cart_items_variant FOREIGN KEY (variant_id) REFERENCES prd_variants(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;