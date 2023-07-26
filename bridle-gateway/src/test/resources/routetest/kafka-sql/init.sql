CREATE TABLE System (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    stream VARCHAR(255)
);

CREATE TABLE Company (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    founded YEAR
);

CREATE TABLE Location (
    id INT PRIMARY KEY,
    company_id INT,
    country VARCHAR(50),
    city VARCHAR(50),
    address VARCHAR(255),
    postal_code VARCHAR(20),
    FOREIGN KEY (company_id) REFERENCES Company(id)
);

CREATE TABLE Executive (
    id INT PRIMARY KEY,
    company_id INT,
    name VARCHAR(255),
    title VARCHAR(255),
    email VARCHAR(255),
    start_date DATE,
    FOREIGN KEY (company_id) REFERENCES Company(id)
);

CREATE TABLE Product (
    id INT PRIMARY KEY,
    company_id INT,
    name VARCHAR(255),
    category VARCHAR(255),
    price DECIMAL(10, 2),
    FOREIGN KEY (company_id) REFERENCES Company(id)
);

CREATE TABLE Version (
    id INT PRIMARY KEY,
    product_id INT,
    version VARCHAR(20),
    release_date DATE,
    FOREIGN KEY (product_id) REFERENCES Product(id)
);
