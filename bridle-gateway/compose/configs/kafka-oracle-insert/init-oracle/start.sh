sqlplus -s testUser/testPassword@//localhost/testDB <<EOF
ALTER SESSION SET CURRENT_SCHEMA = TESTUSER;

CREATE TABLE company (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR2(255),
    founded INTEGER
);
EOF
