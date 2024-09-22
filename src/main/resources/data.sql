-- INSERT INTO restaurant (name, address, city, state, zip_code, cuisine_type, rating, opening_hours, phone_number,
--                         website, is_open)
-- VALUES ('The Fancy Fork', '123 Culinary Street', 'San Francisco', 'CA', '94110', 'Italian', 4.7, '9:00 AM - 10:00 PM',
--         '123-456-7890', 'http://fancyfork.com', TRUE),
--        ('Spicy Spoon', '456 Flavor Avenue', 'Los Angeles', 'CA', '90001', 'Indian', 4.5, '10:00 AM - 11:00 PM',
--         '987-654-3210', 'http://spicyspoon.com', FALSE);

INSERT INTO restaurant (name, address, city, state, zip_code, cuisine_type, rating, opening_hours, phone_number, website, is_open, max_capacity)
VALUES
    ('The Fancy Fork', '123 Culinary Street', 'San Francisco', 'CA', '94110', 'Italian, French', 4.7, '9:00 AM - 10:00 PM', '123-456-7890', 'http://fancyfork.com', TRUE, 1),
    ('Spicy Spoon', '456 Flavor Avenue', 'Los Angeles', 'CA', '90001', 'Indian, Thai', 4.5, '10:00 AM - 11:00 PM', '987-654-3210', 'http://spicyspoon.com', FALSE, 1),
    ('Global Gourmet', '789 World Drive', 'Austin', 'TX', '73301', 'Mexican, American', 4.6, '10:00 AM - 9:00 PM', '123-789-4560', 'http://globalgourmet.com', TRUE, 1),
    ('Sushi & Grill Fusion', '987 Ocean Drive', 'Miami', 'FL', '33101', 'Japanese, Korean', 4.8, '11:00 AM - 10:00 PM', '321-654-9870', 'http://sushigrillfusion.com', TRUE, 10),
    ('Vegan & Raw Delight', '321 Green Blvd', 'Portland', 'OR', '97035', 'Vegan, Raw', 4.3, '8:00 AM - 8:00 PM', '321-456-7891', 'http://veganrawdelight.com', TRUE, 25),
    ('Pasta & Pizza Palace', '654 Noodle Lane', 'Chicago', 'IL', '60601', 'Italian, Pizza', 4.9, '12:00 PM - 11:00 PM', '654-789-3211', 'http://pastapizzapalace.com', TRUE, 60),
    ('Taco & Burrito Town', '876 Fiesta Avenue', 'Phoenix', 'AZ', '85001', 'Mexican, Tex-Mex', 4.4, '9:00 AM - 9:00 PM', '654-321-7890', 'http://tacoburritotown.com', FALSE, 35),
    ('Spice & Curry Corner', '567 Spice Street', 'Seattle', 'WA', '98101', 'Indian, Middle Eastern', 4.7, '10:00 AM - 11:00 PM', '987-123-6540', 'http://spicecurrycorner.com', TRUE, 50),
    ('Pizza & Pasta Paradise', '123 Slice Road', 'New York', 'NY', '10001', 'Italian, Pizza', 4.6, '11:00 AM - 12:00 AM', '123-654-9871', 'http://pizzapastaparadise.com', TRUE, 70),
    ('BBQ & Grill Haven', '321 Smoke Blvd', 'Memphis', 'TN', '37501', 'BBQ, American', 4.8, '10:00 AM - 10:00 PM', '654-987-1232', 'http://bbqgrillhaven.com', TRUE, 55),
    ('Dim Sum & Sushi Dynasty', '432 Bamboo Way', 'San Francisco', 'CA', '94110', 'Chinese, Japanese', 4.5, '10:30 AM - 10:30 PM', '654-123-7891', 'http://dimsumsushidynasty.com', TRUE, 65);

-- Menu items for The Fancy Fork
INSERT INTO menu_item (restaurant_id, name, description, price, available, created_at, updated_at)
VALUES
    (1, 'Spaghetti Carbonara', 'Classic Italian pasta dish with eggs, cheese, pancetta, and pepper.', 15.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 'Margherita Pizza', 'Pizza with fresh mozzarella, basil, and tomato sauce.', 14.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Menu items for Spicy Spoon
INSERT INTO menu_item (restaurant_id, name, description, price, available, created_at, updated_at)
VALUES
    (2, 'Chicken Tikka Masala', 'Chicken cooked in creamy tomato curry sauce.', 12.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Lamb Vindaloo', 'Spicy Indian lamb curry with vinegar and garlic.', 14.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Menu items for Global Gourmet
INSERT INTO menu_item (restaurant_id, name, description, price, available, created_at, updated_at)
VALUES
    (3, 'Cheeseburger', 'Grilled beef patty with cheese, lettuce, and tomato.', 9.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'Tacos Al Pastor', 'Mexican tacos with marinated pork and pineapple.', 11.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Menu items for Sushi & Grill Fusion
INSERT INTO menu_item (restaurant_id, name, description, price, available, created_at, updated_at)
VALUES
    (4, 'California Roll', 'Sushi roll with crab, avocado, and cucumber.', 8.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 'Spicy Tuna Roll', 'Sushi roll with spicy tuna and cucumber.', 9.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Menu items for Vegan & Raw Delight
INSERT INTO menu_item (restaurant_id, name, description, price, available, created_at, updated_at)
VALUES
    (5, 'Avocado Toast', 'Toast with mashed avocado and olive oil.', 6.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'Vegan Burger', 'Plant-based burger with lettuce and tomato.', 9.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Menu items for Pasta & Pizza Palace
INSERT INTO menu_item (restaurant_id, name, description, price, available, created_at, updated_at)
VALUES
    (6, 'Pepperoni Pizza', 'Pizza with pepperoni and mozzarella.', 12.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 'Lasagna', 'Layered pasta with meat and cheese.', 14.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Menu items for Taco & Burrito Town
INSERT INTO menu_item (restaurant_id, name, description, price, available, created_at, updated_at)
VALUES
    (7, 'Beef Tacos', 'Soft tacos with seasoned beef.', 8.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (7, 'Carnitas Tacos', 'Tacos with slow-cooked pork.', 9.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Menu items for Spice & Curry Corner
INSERT INTO menu_item (restaurant_id, name, description, price, available, created_at, updated_at)
VALUES
    (8, 'Green Curry', 'Thai green curry with chicken and coconut milk.', 12.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (8, 'Butter Naan', 'Soft Indian flatbread.', 2.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Menu items for Pizza & Pasta Paradise
INSERT INTO menu_item (restaurant_id, name, description, price, available, created_at, updated_at)
VALUES
    (9, 'Four Cheese Pizza', 'Pizza with mozzarella, cheddar, gorgonzola, and parmesan.', 13.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (9, 'Spaghetti Bolognese', 'Pasta with meat sauce.', 13.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Menu items for BBQ & Grill Haven
INSERT INTO menu_item (restaurant_id, name, description, price, available, created_at, updated_at)
VALUES
    (10, 'BBQ Ribs', 'Slow-cooked ribs with BBQ sauce.', 16.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (10, 'Buffalo Wings', 'Spicy chicken wings with blue cheese dip.', 10.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Menu items for Dim Sum & Sushi Dynasty
INSERT INTO menu_item (restaurant_id, name, description, price, available, created_at, updated_at)
VALUES
    (11, 'Pork Dumplings', 'Steamed dumplings filled with pork.', 7.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (11, 'Sushi Sashimi Platter', 'Assorted raw fish slices.', 19.99, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO customers (name, email, phone_number, address)
VALUES
    ('Alice Johnson', 'alice.johnson@example.com', '555-1234', '123 Maple Street, Springfield, IL'),
    ('Bob Smith', 'bob.smith@example.com', '555-5678', '456 Oak Avenue, Denver, CO'),
    ('Charlie Brown', 'charlie.brown@example.com', '555-9876', '789 Pine Road, Austin, TX');