-- Create t_orders table
CREATE TABLE t_orders
(
    id           BIGINT NOT NULL AUTO_INCREMENT,
    order_number VARCHAR(255),
    PRIMARY KEY (id)
);

-- Create t_order_line_items table
CREATE TABLE t_order_line_items
(
    id       BIGINT NOT NULL AUTO_INCREMENT,
    sku_code VARCHAR(255),
    price    DECIMAL(19, 2),
    quantity INTEGER,
    order_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_order_line_items_order FOREIGN KEY (order_id) REFERENCES t_orders (id)
);

-- Create index on order_id for better performance
CREATE INDEX idx_order_line_items_order_id ON t_order_line_items (order_id);