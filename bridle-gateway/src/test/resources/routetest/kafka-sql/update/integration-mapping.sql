UPDATE COMPANY 
SET FOUNDED = :#${body['company']['founded']}
WHERE NAME = :#${body['company']['name']}