-- schema.sql
-- DROP TABLE restaurant;
-- CREATE TABLE restaurant
-- (
--     id            BIGINT AUTO_INCREMENT PRIMARY KEY,
--     name          VARCHAR(255) NOT NULL,
--     address       VARCHAR(255) NOT NULL,
--     city          VARCHAR(100),
--     state         VARCHAR(100),
--     zip_code      VARCHAR(20),
--     cuisine_type  VARCHAR(100),
--     rating        DECIMAL(3, 2),
--     opening_hours VARCHAR(255),
--     phone_number  VARCHAR(20),
--     website       VARCHAR(255),
--     is_open       BOOLEAN      NOT NULL DEFAULT TRUE,
--     created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     updated_at    TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- );

-- Restaurant Table
CREATE TABLE restaurant
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(255) NOT NULL,
    address           VARCHAR(255) NOT NULL,
    city              VARCHAR(100),
    state             VARCHAR(100),
    zip_code          VARCHAR(20),
    cuisine_type      VARCHAR(100),
    rating            DECIMAL(3, 2),
    opening_hours     VARCHAR(255),
    phone_number      VARCHAR(20),
    website           VARCHAR(255),
    is_open           BOOLEAN NOT NULL DEFAULT TRUE,
    max_capacity      INT NOT NULL DEFAULT 50,
    current_processing_load INT NOT NULL DEFAULT 0,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


-- -- Menu Item Table
CREATE TABLE menu_item
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    price         DECIMAL(10, 2) NOT NULL,
    available     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant(id) ON DELETE CASCADE
);

CREATE TABLE customers
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Orders Table
CREATE TABLE orders
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Order Items Table
CREATE TABLE order_items
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_item_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_item(id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurant(id)
);
