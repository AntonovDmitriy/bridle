DELETE FROM COMPANY WHERE NAME = :#${body['company']['name']} AND FOUNDED = :#${body['company']['founded']}
