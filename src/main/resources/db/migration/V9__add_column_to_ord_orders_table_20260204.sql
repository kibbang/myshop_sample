-- =========================================
-- 주문(ord_orders)에 "배송지 스냅샷" 컬럼 추가
-- MySQL / InnoDB 기준
-- =========================================

-- ✅ 운영/기존 데이터가 이미 있다면:
-- NOT NULL 컬럼을 바로 추가하면 실패할 수 있어서
-- 1) NULL 허용으로 추가 → 2) 기존 row 채움 → 3) NOT NULL로 변경
-- 순서로 가는 걸 추천

-- 1) 컬럼 추가 (일단 NULL 허용)
ALTER TABLE ord_orders
    ADD COLUMN receiver_name      VARCHAR(50)  NULL,
    ADD COLUMN receiver_phone     VARCHAR(20)  NULL,
    ADD COLUMN receiver_zip       VARCHAR(10)  NULL,
    ADD COLUMN receiver_base_address  VARCHAR(255) NULL,
    ADD COLUMN receiver_detail_address  VARCHAR(255) NULL,
    ADD COLUMN delivery_memo      VARCHAR(200) NULL;

-- 2) (선택) 기존 데이터가 있다면 임시값으로 채우기
--    - 주문 테이블에 기존 row가 없으면 이 UPDATE는 생략 가능
UPDATE ord_orders
SET
    receiver_name     = COALESCE(receiver_name, ''),
    receiver_phone    = COALESCE(receiver_phone, ''),
    receiver_zip      = COALESCE(receiver_zip, ''),
    receiver_base_address = COALESCE(receiver_base_address, ''),
    receiver_detail_address = COALESCE(receiver_detail_address, '')
WHERE
    receiver_name IS NULL
   OR receiver_phone IS NULL
   OR receiver_zip IS NULL
   OR receiver_base_address IS NULL
   OR receiver_detail_address IS NULL;

-- 3) NOT NULL로 강제 (MVP에서 주문은 배송지 필수로 받을 거라면)
ALTER TABLE ord_orders
    MODIFY receiver_name      VARCHAR(50)  NOT NULL,
    MODIFY receiver_phone     VARCHAR(20)  NOT NULL,
    MODIFY receiver_zip       VARCHAR(10)  NOT NULL,
    MODIFY receiver_base_address  VARCHAR(255) NOT NULL,
    MODIFY receiver_detail_address  VARCHAR(255) NOT NULL;

-- delivery_memo는 끝까지 NULL 허용(선택 입력)
