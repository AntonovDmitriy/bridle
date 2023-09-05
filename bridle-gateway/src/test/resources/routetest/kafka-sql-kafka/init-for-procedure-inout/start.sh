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

CREATE OR REPLACE PROCEDURE merge_person (
    p_id IN OUT person.id%TYPE,
    p_first_name IN OUT person.first_name%TYPE,
    p_last_name IN OUT person.last_name%TYPE,
    p_age IN OUT person.age%TYPE,
    p_city IN OUT person.city%TYPE,
    p_occupation IN OUT person.occupation%TYPE
) AS
BEGIN
    IF p_id IS NOT NULL THEN
        BEGIN
            SELECT id INTO p_id FROM person WHERE id = p_id;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RAISE_APPLICATION_ERROR(-20001, 'ID ' || p_id || ' не существует в таблице person');
        END;

        -- Update logic
        UPDATE person SET
            first_name = COALESCE(p_first_name, first_name),
            last_name = COALESCE(p_last_name, last_name),
            age = COALESCE(p_age, age),
            city = COALESCE(p_city, city),
            occupation = COALESCE(p_occupation, occupation)
        WHERE id = p_id;

        -- Retrieve all fields after update
        SELECT first_name, last_name, age, city, occupation
        INTO p_first_name, p_last_name, p_age, p_city, p_occupation
        FROM person
        WHERE id = p_id;

    ELSE
        -- Insert logic
        INSERT INTO person (first_name, last_name, age, city, occupation)
        VALUES (p_first_name, p_last_name, p_age, p_city, p_occupation)
        RETURNING id, first_name, last_name, age, city, occupation
        INTO p_id, p_first_name, p_last_name, p_age, p_city, p_occupation;
    END IF;
END;
/




INSERT INTO person (first_name, last_name, age, city, occupation)
VALUES ('John', 'Doe', 30, 'New York', 'Engineer');

INSERT INTO person (first_name, last_name, age, city, occupation)
VALUES ('Jane', 'Smith', 25, 'Los Angeles', 'Doctor');

INSERT INTO person (first_name, last_name, age, city, occupation)
VALUES ('Bob', 'Johnson', 35, 'San Francisco', 'Teacher');
EOF
