sqlplus -s testUser/testPassword@//localhost/testDB <<EOF
ALTER SESSION SET CURRENT_SCHEMA = TESTUSER;

CREATE TABLE clob_data (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    data CLOB
);

CREATE OR REPLACE PROCEDURE save_and_return_clob(
    p_input_clob IN CLOB,
    p_output_clob OUT CLOB
) IS
    v_id NUMBER;
BEGIN
    INSERT INTO clob_data (data)
    VALUES (p_input_clob)
    RETURNING id INTO v_id;

    SELECT data INTO p_output_clob
    FROM clob_data
    WHERE id = v_id;
END save_and_return_clob;
/

EOF
