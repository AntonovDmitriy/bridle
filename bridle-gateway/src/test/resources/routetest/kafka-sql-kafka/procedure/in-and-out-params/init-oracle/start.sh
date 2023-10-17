sqlplus -s testUser/testPassword@//localhost/testDB <<EOF
ALTER SESSION SET CURRENT_SCHEMA = TESTUSER;

CREATE TABLE person (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR2(255),
    last_name VARCHAR2(255),
    age NUMBER,
    city VARCHAR2(255),
    occupation VARCHAR2(255)
);

CREATE OR REPLACE PROCEDURE select_person (
    p_first_name IN person.first_name%TYPE DEFAULT NULL,
    p_last_name IN person.last_name%TYPE DEFAULT NULL,
    OUT_age OUT person.age%TYPE,
    OUT_city OUT person.city%TYPE,
    OUT_occupation OUT person.occupation%TYPE
) AS
BEGIN
    SELECT age, city, occupation
    INTO OUT_age, OUT_city, OUT_occupation
    FROM person
    WHERE
        (first_name = p_first_name) AND
        (last_name = p_last_name);
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        NULL;
    WHEN TOO_MANY_ROWS THEN
        NULL;
END;
/

INSERT INTO person (first_name, last_name, age, city, occupation)
VALUES ('John', 'Doe', 30, 'New York', 'Engineer');

INSERT INTO person (first_name, last_name, age, city, occupation)
VALUES ('Jane', 'Smith', 25, 'Los Angeles', 'Doctor');

INSERT INTO person (first_name, last_name, age, city, occupation)
VALUES ('Bob', 'Johnson', 35, 'San Francisco', 'Teacher');
EOF
