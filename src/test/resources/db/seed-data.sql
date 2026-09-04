INSERT INTO users (username, email, first_name, last_name, role, status) VALUES
('emilys', 'emily.johnson@x.dummyjson.com', 'Emily', 'Johnson', 'ADMIN', 'ACTIVE'),
('michaelw', 'michael.williams@x.dummyjson.com', 'Michael', 'Williams', 'USER', 'ACTIVE'),
('sophiab', 'sophia.brown@x.dummyjson.com', 'Sophia', 'Brown', 'USER', 'INACTIVE'),
('jamesd', 'james.davis@x.dummyjson.com', 'James', 'Davis', 'MODERATOR', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;

INSERT INTO products (title, description, price, category, stock) VALUES
('Essence Mascara Lash Princess', 'The Essence Mascara Lash Princess is a popular mascara known for its volumizing effect.', 9.99, 'beauty', 99),
('Eyeshadow Palette with Mirror', 'The Eyeshadow Palette with Mirror offers a versatile range of eyeshadow shades.', 19.99, 'beauty', 45),
('Powder Canister', 'The Powder Canister is a finely milled setting powder designed to set makeup.', 14.99, 'beauty', 60),
('Red Lipstick', 'The Red Lipstick is a classic and bold choice for adding a pop of color.', 12.99, 'beauty', 80),
('Calvin Klein CK One', 'CK One by Calvin Klein is a classic unisex fragrance.', 49.99, 'fragrances', 30)
ON CONFLICT DO NOTHING;

INSERT INTO orders (user_id, product_id, quantity, total_amount, status) VALUES
(1, 1, 2, 19.98, 'COMPLETED'),
(1, 5, 1, 49.99, 'PROCESSING'),
(2, 2, 1, 19.99, 'PENDING')
ON CONFLICT DO NOTHING;
