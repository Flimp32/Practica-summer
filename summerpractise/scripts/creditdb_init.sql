CREATE TABLE IF NOT EXISTS СКИ (
                     id SERIAL PRIMARY KEY,
                     passport_number INT,
                     bank_name VARCHAR(100),
                     credit INT,
                     dolg INT
);


INSERT INTO СКИ (passport_number, bank_name, credit, dolg) VALUES
('123456789', 'Bank A', 10000, 500),
('123456789', 'Bank B', 15000, 14000),
('987654321', 'Bank B', 15000, 0);