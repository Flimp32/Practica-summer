CREATE TABLE IF NOT EXISTS СДОЧ (
                      id SERIAL PRIMARY KEY,
                      first_name VARCHAR(100),
                      last_name VARCHAR(100),
                      age INT,
                      gender VARCHAR(10),
                      passport_number INT
);

INSERT INTO СДОЧ (first_name, last_name, age, gender, passport_number) VALUES
('Ivan', 'Ivanov', 30, 'Men', '123456789'),
('Marie', 'Petrova', 25, 'Women', '987654321'),
('Den', 'Petrov', 22, 'Men', '123456798');

