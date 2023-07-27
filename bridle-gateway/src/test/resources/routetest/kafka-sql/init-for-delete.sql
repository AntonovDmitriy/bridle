-- Company table
CREATE TABLE company (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR2(255),
    founded INTEGER
);

INSERT INTO COMPANY (NAME, FOUNDED) VALUES ('XYZ Corp', 1995)
--
---- Company table

--CREATE TABLE company2 (
--    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
--    name VARCHAR2(255),
--    founded NUMBER(4)
--);
--
--CREATE TABLE products (
--    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
--    company_id NUMBER,
--    name VARCHAR2(255),
--    category VARCHAR2(255),
--    price NUMBER(10,2),
--    FOREIGN KEY (company_id) REFERENCES company(id)
--);