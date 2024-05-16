
sqlplus -s testUser/testPassword@//localhost/testDB <<EOF

ALTER SESSION SET CURRENT_SCHEMA = TESTUSER;

CREATE TABLE person (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR2(255),
    last_name VARCHAR2(255),
    age NUMBER,
    city VARCHAR2(255),
    occupation VARCHAR2(255)
);

CREATE OR REPLACE PROCEDURE insert_into_person (
    p_first_name IN person.first_name%TYPE,
    p_last_name IN person.last_name%TYPE,
    p_age IN person.age%TYPE,
    p_city IN person.city%TYPE,
    p_occupation IN person.occupation%TYPE
) AS
BEGIN
    DBMS_LOCK.SLEEP(100);

    INSERT INTO person (first_name, last_name, age, city, occupation)
    VALUES (p_first_name, p_last_name, p_age, p_city, p_occupation);
END;
/
EOF
