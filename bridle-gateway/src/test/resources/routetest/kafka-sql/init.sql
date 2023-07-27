-- System table
CREATE TABLE system (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR2(255),
    stream VARCHAR2(255)
);

-- Company table
CREATE TABLE company (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR2(255),
    founded NUMBER(4)
);

-- Locations table
CREATE TABLE locations (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_id NUMBER,
    country VARCHAR2(255),
    city VARCHAR2(255),
    address VARCHAR2(255),
    postal_code VARCHAR2(20),
    FOREIGN KEY (company_id) REFERENCES company(id)
);

-- Executives table
CREATE TABLE executives (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_id NUMBER,
    name VARCHAR2(255),
    title VARCHAR2(255),
    email VARCHAR2(255),
    start_date DATE,
    FOREIGN KEY (company_id) REFERENCES company(id)
);

-- Products table
CREATE TABLE products (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_id NUMBER,
    name VARCHAR2(255),
    category VARCHAR2(255),
    price NUMBER(10,2),
    FOREIGN KEY (company_id) REFERENCES company(id)
);

-- Versions table
CREATE TABLE versions (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id NUMBER,
    version VARCHAR2(10),
    release_date DATE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);
