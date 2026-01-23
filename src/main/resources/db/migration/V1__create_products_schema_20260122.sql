-- MySQL 8.x / InnoDB / utf8mb4
SET NAMES utf8mb4;

-- FK 때문에 의존관계 역순으로 DROP
DROP TABLE IF EXISTS prd_variant_option_values;
DROP TABLE IF EXISTS prd_option_values;
DROP TABLE IF EXISTS prd_options;
DROP TABLE IF EXISTS prd_inventories;
DROP TABLE IF EXISTS prd_variants;
DROP TABLE IF EXISTS prd_products;

-- 1) prd_products
CREATE TABLE prd_products (
                              id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                              product_code  VARCHAR(50) NULL,
                              name          VARCHAR(200) NOT NULL,
                              slug          VARCHAR(200) NULL,
                              description   TEXT NULL,
                              status        VARCHAR(20) NOT NULL,         -- DRAFT|ACTIVE|INACTIVE
                              base_price    INT UNSIGNED NOT NULL,
                              currency      VARCHAR(3) NOT NULL DEFAULT 'KRW',
                              created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (id),
                              UNIQUE KEY uk_prd_products_product_code (product_code),
                              UNIQUE KEY uk_prd_products_slug (slug),
                              KEY idx_prd_products_status (status),
                              CONSTRAINT chk_prd_products_status CHECK (status IN ('DRAFT','ACTIVE','INACTIVE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2) prd_variants
CREATE TABLE prd_variants (
                              id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                              product_id   BIGINT UNSIGNED NOT NULL,
                              sku          VARCHAR(100) NOT NULL,
                              status       VARCHAR(20) NOT NULL,          -- ACTIVE|INACTIVE
                              is_default   TINYINT(1) NOT NULL DEFAULT 0,
                              price        INT UNSIGNED NULL,             -- null이면 prd_products.base_price 사용
                              sale_price   INT UNSIGNED NULL,
                              created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (id),
                              UNIQUE KEY uk_prd_variants_sku (sku),
                              KEY idx_prd_variants_product_id (product_id),
                              KEY idx_prd_variants_is_default (product_id, is_default),
                              CONSTRAINT fk_prd_variants_prd_products
                                  FOREIGN KEY (product_id) REFERENCES prd_products (id),
                              CONSTRAINT chk_prd_variants_status CHECK (status IN ('DRAFT','ACTIVE','INACTIVE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3) prd_inventories (variant_id 공유 PK)
CREATE TABLE prd_inventories (
                                 variant_id        BIGINT UNSIGNED NOT NULL,
                                 stock_quantity    INT UNSIGNED NOT NULL DEFAULT 0,
                                 reserved_quantity INT UNSIGNED NOT NULL DEFAULT 0,
                                 safety_stock      INT UNSIGNED NOT NULL DEFAULT 0,
                                 updated_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (variant_id),
                                 CONSTRAINT fk_prd_inventories_prd_variants
                                     FOREIGN KEY (variant_id) REFERENCES prd_variants (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4) prd_options
CREATE TABLE prd_options (
                             id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                             product_id  BIGINT UNSIGNED NOT NULL,
                             name        VARCHAR(100) NOT NULL,          -- Color, Size 등
                             sort_order  INT NOT NULL DEFAULT 0,
                             created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (id),
                             KEY idx_prd_options_product_id (product_id),
                             CONSTRAINT fk_prd_options_prd_products
                                 FOREIGN KEY (product_id) REFERENCES prd_products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5) prd_option_values
CREATE TABLE prd_option_values (
                                   id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                   option_id   BIGINT UNSIGNED NOT NULL,       -- prd_options.id
                                   value       VARCHAR(100) NOT NULL,          -- Red, M 등
                                   sort_order  INT NOT NULL DEFAULT 0,
                                   created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (id),
                                   KEY idx_prd_option_values_option_id (option_id),
                                   CONSTRAINT fk_prd_option_values_prd_options
                                       FOREIGN KEY (option_id) REFERENCES prd_options (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 6) prd_variant_option_values (매핑 테이블, 복합 PK)
CREATE TABLE prd_variant_option_values (
                                           variant_id      BIGINT UNSIGNED NOT NULL,
                                           option_value_id BIGINT UNSIGNED NOT NULL,
                                           created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           PRIMARY KEY (variant_id, option_value_id),
                                           KEY idx_prd_variant_option_values_option_value_id (option_value_id),
                                           CONSTRAINT fk_prd_vov_prd_variants
                                               FOREIGN KEY (variant_id) REFERENCES prd_variants (id),
                                           CONSTRAINT fk_prd_vov_prd_option_values
                                               FOREIGN KEY (option_value_id) REFERENCES prd_option_values (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
