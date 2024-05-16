sqlplus -s testUser/testPassword@//localhost/testDB <<EOF
ALTER SESSION SET CURRENT_SCHEMA = TESTUSER;

CREATE OR REPLACE FUNCTION add_numbers(
    p_number1 IN NUMBER,
    p_number2 IN NUMBER
) RETURN NUMBER IS
BEGIN
    RETURN p_number1 + p_number2;
END add_numbers;
/

EOF
