ALTER TABLE prd_products ADD COLUMN deleted_at DATETIME NULL;
ALTER TABLE prd_variants ADD COLUMN deleted_at DATETIME NULL;
ALTER TABLE prd_inventories ADD COLUMN deleted_at DATETIME NULL;
ALTER TABLE prd_options ADD COLUMN deleted_at DATETIME NULL;
ALTER TABLE prd_option_values ADD COLUMN deleted_at DATETIME NULL;
ALTER TABLE prd_variant_option_values ADD COLUMN deleted_at DATETIME NULL;