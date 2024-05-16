sqlplus -s testUser/testPassword@//localhost/testDB <<EOF
ALTER SESSION SET CURRENT_SCHEMA = TESTUSER;

CREATE OR REPLACE FUNCTION add_numbers_with_difference(
    p_number1 IN NUMBER,
    p_number2 IN NUMBER,
    p_difference OUT NUMBER
) RETURN NUMBER IS
BEGIN
    p_difference := p_number1 - p_number2;
    RETURN p_number1 + p_number2;
END add_numbers_with_difference;
/

EOF
