-- ===========================================================================
-- ERP System — MySQL schema
-- Used by the dedicated MySQL database servers (separate VMs / EC2 instances).
-- In dev the app auto-creates tables via JPA (H2); in prod JPA only validates,
-- so this script is the source of truth for the production database.
-- ===========================================================================

CREATE DATABASE IF NOT EXISTS erpdb
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE erpdb;

-- ---------------------------------------------------------------------------
-- Customers
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS customers (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    phone       VARCHAR(50),
    address     VARCHAR(255),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------------
-- Products / Inventory
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku            VARCHAR(64) NOT NULL UNIQUE,
    name           VARCHAR(255) NOT NULL,
    description    VARCHAR(1000),
    category       VARCHAR(100),
    price          DECIMAL(12,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_products_stock (stock_quantity)
);

-- ---------------------------------------------------------------------------
-- Orders
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id   BIGINT NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount  DECIMAL(12,2) NOT NULL DEFAULT 0,
    order_date    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    INDEX idx_orders_customer (customer_id),
    INDEX idx_orders_status (status)
);

-- ---------------------------------------------------------------------------
-- Order line items
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_items (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id     BIGINT NOT NULL,
    product_id   BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity     INT NOT NULL,
    unit_price   DECIMAL(12,2) NOT NULL,
    subtotal     DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_items_order   FOREIGN KEY (order_id)   REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_items_product FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_items_order (order_id)
);
